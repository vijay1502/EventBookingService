package com.digibusy.EventEverBookingService.Repository;

import com.digibusy.EventEverBookingService.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
List<Booking> findByUserId(String userId);
}
