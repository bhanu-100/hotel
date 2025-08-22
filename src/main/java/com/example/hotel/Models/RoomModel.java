package com.example.hotel.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_number", "hotel_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Room number (e.g., 101, 202A)
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    // Type of room (Single, Double, Suite, Deluxe)
    @Column(nullable = false)
    private String roomType;

    // Price per night
    @Column(nullable = false)
    private Double pricePerNight;

    // Whether the room is currently available
    @Column(nullable = false)
    private boolean available = true;

    // Number of guests allowed
    private int capacity;

    // Floor number
    private int floorNumber;

    // Additional features (AC, WiFi, TV, etc.)
    private String amenities;

    // Soft delete flag
    private boolean isActive = true;

    // Relationship: Each room belongs to a hotel
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelModel hotel;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
