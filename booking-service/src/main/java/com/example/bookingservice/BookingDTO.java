package com.example.bookingservice;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private Long roomId;
    private String username; // Заменяем userId на username
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}