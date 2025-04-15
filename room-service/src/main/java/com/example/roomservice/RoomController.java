package com.example.roomservice;

import com.example.roomservice.RoomDTO;
import com.example.roomservice.RoomRequestDTO;
import com.example.roomservice.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomRequestDTO roomRequestDTO) {
        Room room = roomService.createRoom(roomRequestDTO);
        RoomDTO roomDTO = convertToDTO(room);
        return ResponseEntity.ok(roomDTO);
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

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelId(@RequestParam Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
}