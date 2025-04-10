package com.example.bookinghotelproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название отеля не может быть пустым")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Адрес не может быть пустым")
    @Column(nullable = false)
    private String address;

    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "hotel_services", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "service")
    private List<String> services;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;
}