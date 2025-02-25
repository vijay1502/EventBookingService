package com.digibusy.EventEverBookingService.Repository;

import com.digibusy.EventEverBookingService.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
    List<Ticket> findByBookingId(Long bookingId);
    @Query(value = "SELECT qr_code FROM ticket WHERE booking_id = :bookingId LIMIT 1", nativeQuery = true)
    String findQrCodeByBookingId(@Param("bookingId") Long bookingId);
}
