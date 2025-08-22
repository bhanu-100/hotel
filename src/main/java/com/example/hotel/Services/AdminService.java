package com.example.hotel.Services;

import com.example.hotel.Models.UserModel;
import com.example.hotel.Models.HotelModel;
import com.example.hotel.Repositories.UserRepository;
import com.example.hotel.Repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    // ✅ Get all users
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Activate or deactivate a user
    public Optional<UserModel> setUserActiveStatus(Long userId, boolean isActive) {
        Optional<UserModel> userOpt = userRepository.findById(userId);
        userOpt.ifPresent(user -> {
            user.setActive(isActive);
            userRepository.save(user);
        });
        return userOpt;
    }

    // ✅ Get all hotels
    public List<HotelModel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // ✅ Approve or deactivate hotel
    public Optional<HotelModel> setHotelActiveStatus(Long hotelId, boolean isActive) {
        Optional<HotelModel> hotelOpt = hotelRepository.findById(hotelId);
        hotelOpt.ifPresent(hotel -> {
            hotel.setActive(isActive); // Make sure HotelModel has 'isActive' field
            hotelRepository.save(hotel);
        });
        return hotelOpt;
    }

    // ✅ Optional: get all active users
    public List<UserModel> getActiveUsers() {
        return userRepository.findAll()
                .stream()
                .filter(UserModel::isActive)
                .toList();
    }

    // ✅ Optional: get all active hotels
    public List<HotelModel> getActiveHotels() {
        return hotelRepository.findAll()
                .stream()
                .filter(HotelModel::isActive)
                .toList();
    }
}
