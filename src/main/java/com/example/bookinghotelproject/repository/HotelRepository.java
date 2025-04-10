package com.example.bookinghotelproject.repository;

import com.example.bookinghotelproject.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByNameAndAddress(String name, String address);
}