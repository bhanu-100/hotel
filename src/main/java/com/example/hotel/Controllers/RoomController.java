package com.example.hotel.Controllers;

import com.example.hotel.DTOs.RoomRequestDTO;
import com.example.hotel.DTOs.RoomResponseDTO;
import com.example.hotel.Services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Room Operations", description = "APIs for managing hotel rooms")
@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Add a new room", description = "Hotel owner can add a new room")
    @PostMapping("/add")
    public ResponseEntity<RoomResponseDTO> addRoom(@Valid @RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO room = roomService.addRoom(roomRequestDTO);
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "Update room info", description = "Update existing room details by room ID")
    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Long roomId,
                                                      @Valid @RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO room = roomService.updateRoom(roomId, roomRequestDTO);
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "Get room by ID", description = "Fetch details of a specific room by its ID")
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long roomId) {
        RoomResponseDTO room = roomService.getRoomById(roomId);
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "Get all rooms for a hotel", description = "List all rooms belonging to a specific hotel")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsByHotel(@PathVariable Long hotelId) {
        List<RoomResponseDTO> rooms = roomService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Search rooms", description = "Search rooms by type, price range, and availability")
    @GetMapping("/search")
    public ResponseEntity<List<RoomResponseDTO>> searchRooms(
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean available
    ) {
        List<RoomResponseDTO> rooms = roomService.searchRooms(roomType, minPrice, maxPrice, available);
        return ResponseEntity.ok(rooms);
    }
}
