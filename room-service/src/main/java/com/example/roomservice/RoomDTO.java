package com.example.roomservice;

import lombok.Data;

@Data
public class RoomDTO {
    private Long id;
    private String type;
    private Double price;
    private Boolean available;
    private Integer capacity;
    private Long hotelId;
}