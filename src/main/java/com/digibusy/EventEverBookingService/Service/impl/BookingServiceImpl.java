package com.digibusy.EventEverBookingService.Service.impl;

import com.digibusy.EventEverBookingService.Configuration.EventFeignClient;
import com.digibusy.EventEverBookingService.Model.*;
import com.digibusy.EventEverBookingService.Repository.BookingRepository;
import com.digibusy.EventEverBookingService.Repository.TicketRepository;
import com.digibusy.EventEverBookingService.Service.BookingService;
import com.digibusy.EventEverBookingService.Utils.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    RestClient restClient;
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getBookingsByUserId(String id) {
        return bookingRepository.findByUserId(id);
    }

    @Override
    public Booking createBooking(Booking booking, int ticketCount) {


        EventResponse eventResponse = restClient
                .mutate().baseUrl("http://localhost:8081")
                .build()
                .get()
                .uri("/event/event/{id}", booking.getEventId())
                .retrieve()
                .body(EventResponse.class);
        if(eventResponse == null){
            throw new RuntimeException("Event Not Found!");
        }
        if(eventResponse.getAvailableSeats() < ticketCount){
            throw new RuntimeException("Not Enough seats available");
        }

        if ("PUBLIC".equalsIgnoreCase(eventResponse.getEventType()) && eventResponse.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            PaymentRequest paymentRequest = new PaymentRequest(
                    booking.getUserId(),
                    booking.getEventId(),
                    eventResponse.getPrice().multiply(BigDecimal.valueOf(ticketCount)) // Total price
            );
            PaymentResponse paymentResponse = restClient
                    .mutate().baseUrl("http://localhost:8083")
                    .build()
                    .post()
                    .uri("/payment/request")
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);

            if (paymentResponse == null || !"PENDING".equals(paymentResponse.getStatus())) {
                throw new RuntimeException("Payment failed! Booking cannot proceed.");
            }

            // ✅ Step 3: Poll Payment Status Before Confirming Booking
            String paymentStatus;
            int retryCount = 0;
            do {
                paymentStatus = restClient
                        .mutate().baseUrl("http://localhost:8083")
                        .build()
                        .get()
                        .uri("/payment/status/{orderId}", paymentResponse.getOrderId())
                        .retrieve()
                        .body(String.class);

                retryCount++;
                try {
                    Thread.sleep(2000); // Wait for 2 seconds before retrying
                } catch (InterruptedException ignored) {}

            } while ("PENDING".equals(paymentStatus) && retryCount < 5);

            if (!"SUCCESS".equals(paymentStatus)) {
                throw new RuntimeException("Payment verification failed! Booking cancelled.");
            }
        }

        Booking savedBooking = bookingRepository.save(booking);
        // Generate a single QR Code for the entire booking
        String qrCodeData = "Booking ID: " + savedBooking.getId() +
                "\nEvent ID: " + savedBooking.getEventId() +
                "\nUser ID: " + savedBooking.getUserId() +
                "\nSeats Booked: " + ticketCount;

        String qrCodeBase64;
        try {
            qrCodeBase64 = QRCodeGenerator.generateQRCodeBase64(qrCodeData, 250, 250);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Code", e);
        }

        for (int i = 0; i < ticketCount; i++) {
            Ticket ticket= new Ticket();
            ticket.setBooking(booking);
                    ticket.setSeatNumber("Seat-" + i);
                    ticket.setQrCode(qrCodeBase64);
            ticketRepository.save(ticket);
        }
        restClient.mutate().baseUrl("http://localhost:8081").build().put()
                .uri(uriBuilder -> uriBuilder.path("/event/{id}/updateSeats")
                        .queryParam("seats", ticketCount)
                        .build(booking.getEventId()))
                .retrieve()
                .toBodilessEntity();

//
//        PaymentRequest paymentRequest = new PaymentRequest(booking.getUserId(), booking.getEventId(), booking.getPrice());
//        PaymentResponse paymentResponse = paymentRestClient.processPayment(paymentRequest);
//
//        if (!"PENDING".equals(paymentResponse.getStatus())) {
//            throw new RuntimeException("Payment failed!");
//        }
//
//        // ✅ Poll Payment Status Before Confirming Booking
//        String paymentStatus;
//        int retryCount = 0;
//        do {
//            paymentStatus = paymentRestClient.getPaymentStatus(paymentResponse.getOrderId());
//            retryCount++;
//            Thread.sleep(2000); // Wait for 2 seconds before retrying
//        } while ("PENDING".equals(paymentStatus) && retryCount < 5);
//
//        if ("SUCCESS".equals(paymentStatus)) {
//            booking.setStatus("CONFIRMED");
//        } else {
//            booking.setStatus("PAYMENT_FAILED");
//        }
//
//        return bookingRepository.save(booking);


        return savedBooking;
    }

    @Override
    public String cancelBooking(Long bookingId) {
        Optional<Booking> byId = bookingRepository.findById(bookingId);
        if(byId.isPresent()){
            long ticketsBookedCount = ticketRepository.findTicketsBookedCount(bookingId);

            try {
                bookingRepository.deleteById(bookingId);
            }catch (Exception ex){
                throw new RuntimeException("Unable to Cancel Tickets");
            }
            try{
                restClient.mutate().baseUrl("http://localhost:8081").build().put()
                        .uri(uriBuilder -> uriBuilder.path("/event/{id}/updateSeats")
                                .queryParam("seats", -ticketsBookedCount)
                                .build(byId.get().getEventId()))
                        .retrieve()
                        .toBodilessEntity();
                return "Booking Cancelled";
            }catch (Exception ex){
                throw new RuntimeException("Unable to update ticket availability number:"+ticketsBookedCount+"Error:"+ ex.getMessage());
            }

        }
        return "Unable to find Booking";
    }
}
