package com.example.hotel.DTOs;

import com.example.hotel.Enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {

    private Long id;
    private Long userId;
    private Long roomId;
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private double totalPrice;
    private BookingStatus status;
}
