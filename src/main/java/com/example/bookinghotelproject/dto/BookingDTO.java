package com.example.bookinghotelproject.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private RoomDTO room;
    private UserDTO user;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}