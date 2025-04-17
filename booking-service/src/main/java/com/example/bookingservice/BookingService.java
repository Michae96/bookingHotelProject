package com.example.bookingservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Long userId;
        try {
            String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest().getHeader("Authorization");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String userServiceUrl = "http://auth-service/auth/users/" + bookingDTO.getUsername();
            userId = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    entity,
                    Long.class
            ).getBody();

            if (userId == null) {
                throw new IllegalArgumentException("User not found in auth-service");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching user from auth-service: " + e.getMessage());
        }

        Booking booking = new Booking();
        booking.setRoomId(bookingDTO.getRoomId());
        booking.setUserId(userId);
        booking.setStartDate(bookingDTO.getStartDate());
        booking.setEndDate(bookingDTO.getEndDate());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setRoomId(booking.getRoomId());

        String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            String userServiceUrl = "http://auth-service/auth/users/id/" + booking.getUserId();
            String username = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            ).getBody();
            dto.setUsername(username);
        } catch (Exception e) {
            dto.setUsername("Unknown");
        }

        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    private String getUsernameByUserId(Long userId) {
        try {
            String userServiceUrl = "http://auth-service/auth/users/id/" + userId;
            return restTemplate.getForObject(userServiceUrl, String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching username from auth-service: " + e.getMessage());
        }
    }

    private Long getUserIdByUsername(String username) {
        try {
            String userServiceUrl = "http://auth-service/auth/users/" + username;
            return restTemplate.getForObject(userServiceUrl, Long.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching userId from auth-service: " + e.getMessage());
        }
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));
        return convertToDTO(booking);
    }

    public List<LocalDate> getAvailableDates(Long roomId) {
        // Получаем все подтвержденные бронирования для комнаты
        List<Booking> existingBookings = bookingRepository
                .findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        roomId, "CONFIRMED", LocalDate.now().plusMonths(3), LocalDate.now());

        // Создаем список всех дат на следующие 3 месяца
        List<LocalDate> allDates = LocalDate.now()
                .datesUntil(LocalDate.now().plusMonths(3))
                .collect(Collectors.toList());

        // Удаляем забронированные даты
        existingBookings.forEach(booking -> {
            List<LocalDate> bookedDates = booking.getStartDate()
                    .datesUntil(booking.getEndDate().plusDays(1))
                    .collect(Collectors.toList());
            allDates.removeAll(bookedDates);
        });

        return allDates;
    }

    public BookingDTO updateBooking(Long bookingId, BookingDTO updateDTO) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Cannot update cancelled booking");
        }

        // Проверяем доступность новых дат
        List<Booking> conflictingBookings = bookingRepository
                .findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        updateDTO.getRoomId(),
                        "CONFIRMED",
                        updateDTO.getEndDate(),
                        updateDTO.getStartDate());

        if (!conflictingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for selected dates");
        }

        booking.setRoomId(updateDTO.getRoomId());
        booking.setStartDate(updateDTO.getStartDate());
        booking.setEndDate(updateDTO.getEndDate());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }


//    public List<BookingDTO> getBookingsByUserId(Long userId) {
//        return bookingRepository.findByUserId(userId).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByRoomId(Long roomId) {
//        return bookingRepository.findByRoomId(roomId).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByStatus(String status) {
//        return bookingRepository.findByStatus(status).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
//        return bookingRepository.findByStartDateBetween(startDate, endDate).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
//        return bookingRepository.findByRoomIdAndStartDateBetween(roomId, startDate, endDate).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
//        return bookingRepository.findByUserIdAndStartDateBetween(userId, startDate, endDate).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByRoomIdAndStatus(Long roomId, String status) {
//        return bookingRepository.findByRoomIdAndStatus(roomId, status).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//    public List<BookingDTO> getBookingsByUserIdAndStatus(Long userId, String status) {
//        return bookingRepository.findByUserIdAndStatus(userId, status).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingDTO> getBookingsByRoomIdAndUserId(Long roomId, Long userId) {
//        return bookingRepository.findByRoomIdAndUserId(roomId, userId).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }


}