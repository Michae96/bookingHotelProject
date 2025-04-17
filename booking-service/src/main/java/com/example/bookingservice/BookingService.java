package com.example.bookingservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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


}