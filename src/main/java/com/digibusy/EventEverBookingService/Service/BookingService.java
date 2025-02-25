package com.digibusy.EventEverBookingService.Service;

import com.digibusy.EventEverBookingService.Model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    public List<Booking> getAllBookings();
    public Optional<Booking> getBookingById(Long id);
    public List<Booking> getBookingsByUserId(String id);
    public Booking createBooking(Booking booking, int ticketCount);
    public String cancelBooking(Long bookingId);
}
