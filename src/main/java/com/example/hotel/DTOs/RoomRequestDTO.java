package com.example.hotel.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequestDTO {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String roomType;

    @NotNull(message = "Price per night is required")
    @Min(value = 0, message = "Price must be positive")
    private Double pricePerNight;

    @NotNull(message = "Availability is required")
    private Boolean available;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    private int floorNumber;

    private String amenities;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;
}
