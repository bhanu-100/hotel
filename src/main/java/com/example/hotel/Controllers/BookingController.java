package com.example.hotel.Controllers;

import com.example.hotel.DTOs.BookingRequestDTO;
import com.example.hotel.DTOs.BookingResponseDTO;
import com.example.hotel.Services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking Controller", description = "Operations related to hotel bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    @Operation(summary = "Create a new booking", description = "Create a booking for a user and a room with check-in/out dates and guest details")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequest) {
        BookingResponseDTO booking = bookingService.createBooking(bookingRequest);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/update-status/{bookingId}")
    @Operation(summary = "Update booking status", description = "Update the status of a booking (e.g., CONFIRMED, CANCELLED, COMPLETED)")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(@PathVariable Long bookingId,
                                                                  @RequestParam String status) {
        BookingResponseDTO booking = bookingService.updateBookingStatus(bookingId, status);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID", description = "Fetch booking details by booking ID")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long bookingId) {
        BookingResponseDTO booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all bookings for a user", description = "Fetch all bookings made by a specific user")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUser(@PathVariable Long userId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all bookings for a hotel", description = "Fetch all bookings for a specific hotel (for hotel owner/admin)")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByHotel(@PathVariable Long hotelId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByHotel(hotelId);
        return ResponseEntity.ok(bookings);
    }
}
