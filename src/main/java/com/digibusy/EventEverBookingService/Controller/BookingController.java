package com.digibusy.EventEverBookingService.Controller;

import com.digibusy.EventEverBookingService.Model.Booking;
import com.digibusy.EventEverBookingService.Model.Ticket;
import com.digibusy.EventEverBookingService.Repository.TicketRepository;
import com.digibusy.EventEverBookingService.Service.BookingService;
import com.digibusy.EventEverBookingService.Utils.QRCodeDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {
@Autowired
    BookingService bookingService;
@Autowired
TicketRepository ticketRepository;

    public BookingController() {
    }

    @GetMapping("/getAllBookings")
    public ResponseEntity<List<Booking>> getAllBookings(){
        return new ResponseEntity<>(bookingService.getAllBookings(), HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getByBookingId(@PathVariable Long id){
        return new ResponseEntity<>(bookingService.getBookingById(id).get(),HttpStatus.FOUND);
    }

    @PostMapping("/bookEvent/{ticketCount}")
    public ResponseEntity<Booking> bookingEvent(@RequestBody Booking booking,@PathVariable int ticketCount){
        return new ResponseEntity<>(bookingService.createBooking(booking,ticketCount),HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/cancelBooking/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable long id){
        return new ResponseEntity<>(bookingService.cancelBooking(id),HttpStatus.OK);
    }
    @GetMapping("/userBooking/{userId}")
    public ResponseEntity<List<Booking>> getBookingByUser(@PathVariable String userId){
        return new ResponseEntity<>(bookingService.getBookingsByUserId(userId),HttpStatus.FOUND);
    }


    @GetMapping("/{bookingId}/tickets")
    public ResponseEntity<List<Ticket>> getTickets(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ticketRepository.findByBookingId(bookingId));
    }

    // ✅ API to fetch a single QR code for a booking
    @GetMapping("/{bookingId}/qr-code")
    public ResponseEntity<String> getBookingQRCode(@PathVariable Long bookingId) {
        String qrCode = ticketRepository.findQrCodeByBookingId(bookingId);
        if (qrCode == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No QR Code found for this booking!");
        }
        return ResponseEntity.ok(qrCode);
    }

    @PostMapping("/decode-qr")
    public ResponseEntity<?> decodeQRCode(@RequestBody String base64Image) {
        try {
            String decodedData = QRCodeDecoder.decodeQRCodeFromBase64(base64Image);
            return ResponseEntity.ok(decodedData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error decoding QR Code: " + e.getMessage());
        }
    }

}

