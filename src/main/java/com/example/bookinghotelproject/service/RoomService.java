package com.example.bookinghotelproject.service;

import com.example.bookinghotelproject.dto.RoomRequestDTO;
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

    public Room createRoom(RoomRequestDTO roomRequestDTO) {
        Hotel hotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel with the specified ID does not exist"));

        Room room = new Room();
        room.setType(roomRequestDTO.getType());
        room.setPrice(roomRequestDTO.getPrice());
        room.setAvailable(roomRequestDTO.getAvailable());
        room.setCapacity(roomRequestDTO.getCapacity());
        room.setHotel(hotel);

        return roomRepository.save(room);
    }
}