package com.example.roomservice;

import lombok.Data;

@Data
public class RoomRequestDTO {
    private String type;
    private Double price;
    private Boolean available;
    private Integer capacity;
    private Long hotelId; // Используем ID отеля вместо объекта HotelDTO
}