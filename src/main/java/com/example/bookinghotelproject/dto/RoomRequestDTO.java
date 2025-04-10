package com.example.bookinghotelproject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequestDTO {
    @NotBlank(message = "Room type cannot be blank")
    private String type;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private double price;

    @NotNull(message = "Availability must be specified")
    private Boolean available;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    @NotNull(message = "Hotel must be specified")
    private HotelRequest hotel;

    @Data
    public static class HotelRequest {
        @NotNull(message = "Hotel ID must be specified")
        private Long id;
    }

    public Long getHotelId() {
        return hotel != null ? hotel.getId() : null;
    }
}