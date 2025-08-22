package com.example.hotel.Controllers;

import com.example.hotel.DTOs.RoomRequestDTO;
import com.example.hotel.DTOs.RoomResponseDTO;
import com.example.hotel.Models.HotelModel;
import com.example.hotel.Services.HotelService;
import com.example.hotel.Services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@Tag(name = "Hotel Controller", description = "Endpoints for hotel owners and admins to manage hotels and rooms")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/check")
    @Operation(summary = "Health check", description = "Verify that Hotel controller is up and running")
    public String show() {
        return "Hey! I am Hotel controller";
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new hotel", description = "Create a new hotel entry")
    public ResponseEntity<HotelModel> addHotel(@RequestBody HotelModel hotel) {
        HotelModel savedHotel = hotelService.saveHotel(hotel);
        return ResponseEntity.ok(savedHotel);
    }

    @PutMapping("/{hotelId}/update")
    @Operation(summary = "Update hotel info", description = "Update details of a specific hotel by ID")
    public ResponseEntity<HotelModel> updateHotel(@PathVariable Long hotelId, @RequestBody HotelModel hotel) {
        HotelModel updatedHotel = hotelService.updateHotel(hotelId, hotel);
        return ResponseEntity.ok(updatedHotel);
    }

    @GetMapping("/{hotelId}/rooms")
    @Operation(summary = "List all rooms of a hotel", description = "Fetch all rooms for a specific hotel")
    public ResponseEntity<List<RoomResponseDTO>> getRooms(@PathVariable Long hotelId) {
        List<RoomResponseDTO> rooms = roomService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/{hotelId}/rooms/add")
    @Operation(summary = "Add a room to a hotel", description = "Create a new room for a specific hotel")
    public ResponseEntity<RoomResponseDTO> addRoom(@PathVariable Long hotelId, @RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO savedRoom = roomService.addRoom(roomRequestDTO);
        return ResponseEntity.ok(savedRoom);
    }
}
