package com.example.bookingservice;

import com.example.bookingservice.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {List<Booking> findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long roomId, String status, LocalDate endDate, LocalDate startDate);
}