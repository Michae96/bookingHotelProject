package com.example.bookinghotelproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelDTO {
    private Long id;
    private String name;
    private String address;
    private String description;
    private List<String> services;
    private List<RoomDTO> rooms;
}