package com.example.hotel.DTOs;

import lombok.Data;

@Data
public class HotelResponseDTO {

    private Long id;
    private String name;
    private String city;
    private String state;
    private String country;
    private String address;
    private Integer numberOfRooms;
    private Integer numberOfFloors;
    private String phoneNumber;
    private String email;
    private String description;
    private Double rating;
}
