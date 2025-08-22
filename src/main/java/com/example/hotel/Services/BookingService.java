package com.example.hotel.Services;

import com.example.hotel.DTOs.BookingRequestDTO;
import com.example.hotel.DTOs.BookingResponseDTO;
import com.example.hotel.Enums.BookingStatus;
import com.example.hotel.Models.BookingModel;
import com.example.hotel.Models.RoomModel;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Repositories.BookingRepository;
import com.example.hotel.Repositories.RoomRepository;
import com.example.hotel.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // 1️⃣ Create a booking
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        UserModel user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        RoomModel room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + dto.getRoomId()));

        if (!isRoomAvailable(room, dto.getCheckInDate(), dto.getCheckOutDate())) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        BookingModel booking = new BookingModel();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());

        long days = dto.getCheckOutDate().toEpochDay() - dto.getCheckInDate().toEpochDay();
        booking.setTotalPrice(days * room.getPricePerNight());
        booking.setStatus(BookingStatus.PENDING);

        BookingModel savedBooking = bookingRepository.save(booking);
        return mapToDTO(savedBooking);
    }

    // 2️⃣ Update booking status (for controller updateStatus)
    public BookingResponseDTO updateBookingStatus(Long bookingId, String status) {
        BookingModel booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        booking.setStatus(BookingStatus.valueOf(status.toUpperCase())); // CONFIRMED, CANCELLED, COMPLETED
        return mapToDTO(bookingRepository.save(booking));
    }

    // 3️⃣ Get booking by ID
    public BookingResponseDTO getBookingById(Long bookingId) {
        BookingModel booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        return mapToDTO(booking);
    }

    // 4️⃣ Get all bookings for a user
    public List<BookingResponseDTO> getBookingsByUser(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return bookingRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 5️⃣ Get all bookings for a hotel
    public List<BookingResponseDTO> getBookingsByHotel(Long hotelId) {
        List<BookingModel> bookings = bookingRepository.findAll()
                .stream()
                .filter(b -> b.getRoom().getHotel().getId().equals(hotelId))
                .collect(Collectors.toList());

        return bookings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper: Check if room is available
    public boolean isRoomAvailable(RoomModel room, LocalDate checkIn, LocalDate checkOut) {
        List<BookingModel> bookings = bookingRepository.findByRoomAndStatusIn(room, List.of(
                BookingStatus.PENDING, BookingStatus.CONFIRMED
        ));
        return bookings.stream().noneMatch(b -> datesOverlap(checkIn, checkOut, b.getCheckInDate(), b.getCheckOutDate()));
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    // Mapper: BookingModel -> BookingResponseDTO
    private BookingResponseDTO mapToDTO(BookingModel booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
