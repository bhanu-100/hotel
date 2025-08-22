package com.example.hotel.DTOs;

import lombok.Data;

@Data
public class RoomResponseDTO {

    private Long id;
    private String roomNumber;
    private String roomType;
    private Double pricePerNight;
    private Boolean available;
    private int capacity;
    private int floorNumber;
    private String amenities;
    private Long hotelId; // reference hotel
}
