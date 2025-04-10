package com.example.bookinghotelproject.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private Long id;
    private String type;
    private double price;
    private boolean available;
    private int capacity;
}