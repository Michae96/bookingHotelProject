package com.example.bookinghotelproject.service;

import com.example.bookinghotelproject.dto.BookingDTO;
import com.example.bookinghotelproject.dto.RoomDTO;
import com.example.bookinghotelproject.dto.UserDTO;
import com.example.bookinghotelproject.entity.Booking;
import com.example.bookinghotelproject.entity.Room;
import com.example.bookinghotelproject.entity.User;
import com.example.bookinghotelproject.repository.BookingRepository;
import com.example.bookinghotelproject.repository.RoomRepository;
import com.example.bookinghotelproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository; // Добавлено

    public BookingDTO createBooking(String username, Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        roomId, "CONFIRMED", endDate, startDate);

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected dates");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus("CONFIRMED");
        booking.setUser(user);

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
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

    public BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());

        // Преобразование Room в RoomDTO
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(booking.getRoom().getId());
        roomDTO.setType(booking.getRoom().getType());
        roomDTO.setPrice(booking.getRoom().getPrice());
        roomDTO.setAvailable(booking.getRoom().isAvailable());
        roomDTO.setCapacity(booking.getRoom().getCapacity());
        dto.setRoom(roomDTO);

        // Преобразование User в UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(booking.getUser().getId());
        userDTO.setUsername(booking.getUser().getUsername());
        dto.setUser(userDTO);

        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());

        return dto;
    }

    public List<LocalDate> getAvailableDatesForRoom(Long roomId) {
        // Получаем текущую дату и дату через месяц
        LocalDate today = LocalDate.now();
        LocalDate oneMonthLater = today.plusMonths(1);

        // Получаем все бронирования для указанного номера
        List<Booking> bookings = bookingRepository.findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                roomId, "CONFIRMED", oneMonthLater, today);

        // Создаем список всех дат в диапазоне
        List<LocalDate> allDates = today.datesUntil(oneMonthLater.plusDays(1)).collect(Collectors.toList());

        // Исключаем занятые даты
        List<LocalDate> unavailableDates = new ArrayList<>();
        for (Booking booking : bookings) {
            unavailableDates.addAll(booking.getStartDate().datesUntil(booking.getEndDate().plusDays(1)).collect(Collectors.toList()));
        }

        // Возвращаем только доступные даты
        allDates.removeAll(unavailableDates);
        return allDates;
    }

}