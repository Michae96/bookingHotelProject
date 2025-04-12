package com.example.bookinghotelproject.service;

import com.example.bookinghotelproject.dto.HotelDTO;
import com.example.bookinghotelproject.dto.RoomDTO;
import com.example.bookinghotelproject.entity.Hotel;
import com.example.bookinghotelproject.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        dto.setDescription(hotel.getDescription());
        dto.setServices(hotel.getServices());
        dto.setRooms(hotel.getRooms().stream().map(this::convertRoomToDTO).collect(Collectors.toList()));
        return dto;
    }

    private RoomDTO convertRoomToDTO(com.example.bookinghotelproject.entity.Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        dto.setAvailable(room.isAvailable());
        dto.setCapacity(room.getCapacity());
        return dto;
    }


    public Hotel createHotel(Hotel hotel) {
        if (hotelRepository.existsByNameAndAddress(hotel.getName(), hotel.getAddress())) {
            throw new IllegalArgumentException("Отель с таким названием и адресом уже существует");
        }
        return hotelRepository.save(hotel);
    }

    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        return convertToDTO(hotel);
    }

    public void deleteHotelById(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new IllegalArgumentException("Hotel not found");
        }
        hotelRepository.deleteById(id);
    }

}