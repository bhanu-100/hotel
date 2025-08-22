package com.example.hotel.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String address;

    @NotNull
    private Integer numberOfRooms;

    @NotNull
    private Integer numberOfFloors;

    private String phoneNumber;
    private String email;
    private String description;
    private Double rating;
}
