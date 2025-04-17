package com.example.hotelservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RestTemplate restTemplate;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
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

        HotelDTO dto = convertToDTO(hotel);

        // Получаем заголовок авторизации
        String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");

        // Настраиваем заголовки для запроса
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Получаем комнаты от room-service
        try {
            List<RoomDTO> rooms = restTemplate.exchange(
                    "http://room-service/rooms/hotel/" + id,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<RoomDTO>>() {}
            ).getBody();

            dto.setRooms(rooms != null ? rooms : Collections.emptyList());
        } catch (Exception e) {
            dto.setRooms(Collections.emptyList());
        }

        return dto;
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        dto.setDescription(hotel.getDescription());
        dto.setServices(hotel.getServices());
        return dto;
    }

    public void deleteHotelById(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new IllegalArgumentException("Hotel not found");
        }
        hotelRepository.deleteById(id);
    }


}