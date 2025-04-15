package com.example.roomservice;

import com.example.roomservice.RoomDTO;
import com.example.roomservice.RoomRequestDTO;
import com.example.roomservice.Room;
import com.example.roomservice.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RestTemplate restTemplate;

    public Room createRoom(RoomRequestDTO roomRequestDTO) {
        // Проверяем, существует ли отель
        String hotelServiceUrl = "http://hotel-service/hotels/" + roomRequestDTO.getHotelId();
        restTemplate.getForObject(hotelServiceUrl, Object.class);

        Room room = new Room();
        room.setType(roomRequestDTO.getType());
        room.setPrice(roomRequestDTO.getPrice());
        room.setAvailable(roomRequestDTO.getAvailable());
        room.setCapacity(roomRequestDTO.getCapacity());
        room.setHotelId(roomRequestDTO.getHotelId());

        return roomRepository.save(room);
    }



    public List<RoomDTO> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return convertToDTO(room);
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        dto.setAvailable(room.getAvailable());
        dto.setCapacity(room.getCapacity());
        dto.setHotelId(room.getHotelId());
        return dto;
    }
}