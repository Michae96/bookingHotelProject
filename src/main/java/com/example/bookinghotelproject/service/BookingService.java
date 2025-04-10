package com.example.bookinghotelproject.service;

import com.example.bookinghotelproject.entity.Booking;
import com.example.bookinghotelproject.entity.Room;
import com.example.bookinghotelproject.repository.BookingRepository;
import com.example.bookinghotelproject.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public Booking createBooking(String username, Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Проверка доступности комнаты
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        roomId, "CONFIRMED", endDate, startDate);

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected dates");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus("CONFIRMED");
        booking.setUser(null); // Здесь можно добавить логику для связывания с пользователем

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long bookingId, Long newRoomId, LocalDate newStartDate, LocalDate newEndDate) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Room newRoom = roomRepository.findById(newRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Проверка доступности новой комнаты
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        newRoomId, "CONFIRMED", newEndDate, newStartDate);

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected dates");
        }

        booking.setRoom(newRoom);
        booking.setStartDate(newStartDate);
        booking.setEndDate(newEndDate);

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}