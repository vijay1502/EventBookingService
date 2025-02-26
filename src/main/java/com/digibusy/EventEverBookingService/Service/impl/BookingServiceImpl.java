package com.digibusy.EventEverBookingService.Service.impl;

import com.digibusy.EventEverBookingService.Configuration.EventFeignClient;
import com.digibusy.EventEverBookingService.Model.Booking;
import com.digibusy.EventEverBookingService.Model.EventResponse;
import com.digibusy.EventEverBookingService.Model.Ticket;
import com.digibusy.EventEverBookingService.Repository.BookingRepository;
import com.digibusy.EventEverBookingService.Repository.TicketRepository;
import com.digibusy.EventEverBookingService.Service.BookingService;
import com.digibusy.EventEverBookingService.Utils.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
//        Booking save = bookingRepository.save(booking);
//        for (int i = 0; i <= ticketCount; i++) {
//            Ticket ticket= Ticket.builder().booking(save).seatNumber("Seat-"+i)
//                    .qrCode("QR-"+save.getId()+"-"+i)
//                    .build();
//
//        }
//        return bookingRepository.save(booking);
//        EventResponse eventResponse=
//                eventFeignClient.getEventById(booking.getEventId());

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
