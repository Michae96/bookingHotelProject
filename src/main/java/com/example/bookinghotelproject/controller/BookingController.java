package com.example.bookinghotelproject.controller;

import com.example.bookinghotelproject.entity.Booking;
import com.example.bookinghotelproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 3.1. POST /book
    @PostMapping("/book")
    public ResponseEntity<Booking> createBooking(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        Long roomId = Long.valueOf((Integer) request.get("roomId"));
        LocalDate startDate = LocalDate.parse((String) request.get("startDate"));
        LocalDate endDate = LocalDate.parse((String) request.get("endDate"));

        Booking booking = bookingService.createBooking(username, roomId, startDate, endDate);
        return ResponseEntity.ok(booking);
    }

    // 3.2. PUT /update-booking
    @PutMapping("/update-booking/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long newRoomId = Long.valueOf((Integer) request.get("newRoomId"));
        LocalDate newStartDate = LocalDate.parse((String) request.get("newStartDate"));
        LocalDate newEndDate = LocalDate.parse((String) request.get("newEndDate"));

        Booking booking = bookingService.updateBooking(id, newRoomId, newStartDate, newEndDate);
        return ResponseEntity.ok(booking);
    }

    // 3.3. DELETE /cancel-booking
    @DeleteMapping("/cancel-booking/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled");
    }

    // 3.4. GET /bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // 3.4. GET /booking/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }
}