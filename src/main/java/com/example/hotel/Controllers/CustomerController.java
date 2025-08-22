package com.example.hotel.Controllers;

import com.example.hotel.Models.HotelModel;
import com.example.hotel.Models.RoomModel;
import com.example.hotel.Services.HotelService;
import com.example.hotel.Services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "Customer Controller", description = "Endpoints for customers to view hotels and rooms")
public class CustomerController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/check")
    @Operation(summary = "Health check", description = "Verify that Customer controller is up and running")
    public String show() {
        return "Hey! I am Customer controller";
    }

    @GetMapping("/hotels")
    @Operation(summary = "List all active hotels", description = "Fetch all hotels that are currently active")
    public ResponseEntity<List<HotelModel>> getAllHotels() {
        List<HotelModel> hotels = hotelService.getAllActiveHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    @Operation(summary = "List available rooms by hotel", description = "Fetch all available rooms for a specific hotel")
    public ResponseEntity<List<RoomModel>> getRoomsByHotel(@PathVariable Long hotelId) {
        List<RoomModel> rooms = roomService.getAvailableRoomsByHotel(hotelId);
        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rooms);
    }
}
