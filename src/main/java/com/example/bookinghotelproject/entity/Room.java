package com.example.bookinghotelproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room type cannot be blank")
    @Pattern(regexp = "Standard|Premium|Luxury|Budget", message = "Room type must be one of the following: Standard, Premium, Luxury, Budget")
    @Column(nullable = false)
    private String type;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Column(nullable = false)
    private double price;

    @NotNull(message = "Availability must be specified")
    @Column(nullable = false)
    private boolean available;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private int capacity;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @NotNull(message = "Hotel must be specified")
    private Hotel hotel;
}