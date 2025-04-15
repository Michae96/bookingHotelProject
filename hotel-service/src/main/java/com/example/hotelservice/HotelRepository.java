package com.example.hotelservice;

import com.example.hotelservice.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByNameAndAddress(String name, String address);
}