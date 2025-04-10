package com.example.bookinghotelproject.service;

import com.example.bookinghotelproject.entity.Hotel;
import com.example.bookinghotelproject.entity.Room;
import com.example.bookinghotelproject.repository.HotelRepository;
import com.example.bookinghotelproject.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public Room createRoom(Room room) {
        // Проверяем, что объект hotel не равен null
        if (room.getHotel() == null || room.getHotel().getId() == null) {
            throw new IllegalArgumentException("Hotel information is missing or invalid");
        }

        // Проверяем, существует ли отель с указанным ID
        Hotel hotel = hotelRepository.findById(room.getHotel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel with the specified ID does not exist"));

        // Устанавливаем объект hotel в room
        room.setHotel(hotel);

        // Сохраняем комнату
        return roomRepository.save(room);
    }
}