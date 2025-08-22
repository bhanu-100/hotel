package com.example.hotel.Services;

import com.example.hotel.DTOs.RoomRequestDTO;
import com.example.hotel.DTOs.RoomResponseDTO;
import com.example.hotel.Models.HotelModel;
import com.example.hotel.Models.RoomModel;
import com.example.hotel.Repositories.HotelRepository;
import com.example.hotel.Repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    // Add a new room (using DTO)
    public RoomResponseDTO addRoom(RoomRequestDTO dto) {
        HotelModel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + dto.getHotelId()));

        RoomModel room = new RoomModel();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setPricePerNight(dto.getPricePerNight());
        room.setAvailable(dto.getAvailable());
        room.setCapacity(dto.getCapacity());
        room.setFloorNumber(dto.getFloorNumber());
        room.setAmenities(dto.getAmenities());
        room.setHotel(hotel);

        RoomModel savedRoom = roomRepository.save(room);
        return mapToDTO(savedRoom);
    }

    // Update existing room
    public RoomResponseDTO updateRoom(Long roomId, RoomRequestDTO dto) {
        RoomModel room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        HotelModel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + dto.getHotelId()));

        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setPricePerNight(dto.getPricePerNight());
        room.setAvailable(dto.getAvailable());
        room.setCapacity(dto.getCapacity());
        room.setFloorNumber(dto.getFloorNumber());
        room.setAmenities(dto.getAmenities());
        room.setHotel(hotel);

        RoomModel updatedRoom = roomRepository.save(room);
        return mapToDTO(updatedRoom);
    }

    // Get room by ID
    public RoomResponseDTO getRoomById(Long roomId) {
        RoomModel room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));
        return mapToDTO(room);
    }

    // Get all rooms for a hotel
    public List<RoomResponseDTO> getRoomsByHotel(Long hotelId) {
        HotelModel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
        return roomRepository.findByHotel(hotel).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Search rooms by type, price range, availability
    public List<RoomResponseDTO> searchRooms(String roomType, Double minPrice, Double maxPrice, Boolean available) {
        List<RoomModel> rooms = roomRepository.findAll();

        return rooms.stream()
                .filter(r -> (roomType == null || r.getRoomType().equalsIgnoreCase(roomType)) &&
                        (minPrice == null || r.getPricePerNight() >= minPrice) &&
                        (maxPrice == null || r.getPricePerNight() <= maxPrice) &&
                        (available == null || r.isAvailable()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Inside RoomService.java
    public List<RoomModel> getAvailableRoomsByHotel(Long hotelId) {
        HotelModel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));
        return roomRepository.findByHotelAndAvailableTrue(hotel);
    }


    // Mapper: RoomModel -> RoomResponseDTO
    private RoomResponseDTO mapToDTO(RoomModel room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setAvailable(room.isAvailable());
        dto.setCapacity(room.getCapacity());
        dto.setFloorNumber(room.getFloorNumber());
        dto.setAmenities(room.getAmenities());
        dto.setHotelId(room.getHotel().getId());
        return dto;
    }
}
