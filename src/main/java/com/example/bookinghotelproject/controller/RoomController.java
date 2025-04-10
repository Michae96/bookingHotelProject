package com.example.bookinghotelproject.controller;

import com.example.bookinghotelproject.dto.RoomRequestDTO;
import com.example.bookinghotelproject.entity.Room;
import com.example.bookinghotelproject.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomRequestDTO roomRequestDTO) {
        Room room = roomService.createRoom(roomRequestDTO);
        return ResponseEntity.ok(room);
    }
}