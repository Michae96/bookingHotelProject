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
    public ResponseEntity<?> createRoom(@RequestBody RoomRequestDTO roomRequestDTO) {
        try {
            Room room = roomService.createRoom(roomRequestDTO);
            return ResponseEntity.ok(convertToDTO(room));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Ошибка при создании комнаты: " + e.getMessage()));
        }
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

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }


    @GetMapping
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelId(@RequestParam Long hotelId) {
        List<RoomDTO> rooms = roomService.getRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }



}

class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}