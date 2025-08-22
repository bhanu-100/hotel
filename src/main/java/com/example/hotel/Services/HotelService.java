package com.example.hotel.Services;

import com.example.hotel.Models.HotelModel;
import com.example.hotel.Repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    // Create a new hotel
    public HotelModel saveHotel(@Valid HotelModel hotel) {
        if (hotelRepository.existsByName(hotel.getName())) {
            throw new RuntimeException("Hotel with this name already exists!");
        }
        hotel.setActive(true); // Default as active
        return hotelRepository.save(hotel);
    }

    // Update an existing hotel
    public HotelModel updateHotel(Long hotelId, @Valid HotelModel hotel) {
        HotelModel existingHotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        existingHotel.setName(hotel.getName());
        existingHotel.setCity(hotel.getCity());
        existingHotel.setState(hotel.getState());
        existingHotel.setCountry(hotel.getCountry());
        existingHotel.setAddress(hotel.getAddress());
        existingHotel.setNumberOfRooms(hotel.getNumberOfRooms());
        existingHotel.setNumberOfFloors(hotel.getNumberOfFloors());
        existingHotel.setPhoneNumber(hotel.getPhoneNumber());
        existingHotel.setEmail(hotel.getEmail());
        existingHotel.setDescription(hotel.getDescription());
        existingHotel.setRating(hotel.getRating());

        return hotelRepository.save(existingHotel);
    }

    // Find a hotel by ID
    public HotelModel findHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));
    }

    // Find all hotels
    public List<HotelModel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // Delete a hotel
    public void deleteHotel(Long hotelId) {
        HotelModel hotel = findHotelById(hotelId);
        hotelRepository.delete(hotel);
    }

    // Find hotels by city
    public List<HotelModel> findHotelsByCity(String city) {
        return hotelRepository.findByCity(city);
    }

    // Find hotels with minimum rating
    public List<HotelModel> findHotelsWithMinRating(double minRating) {
        return hotelRepository.findHotelsWithMinRating(minRating);
    }

    // Find all active hotels
    public List<HotelModel> getAllActiveHotels() {
        return hotelRepository.findByIsActiveTrue();
    }

}
