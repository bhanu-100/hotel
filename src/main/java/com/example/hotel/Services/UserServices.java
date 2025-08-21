package com.example.hotel.Services;

import com.example.hotel.Enums.UserRoles;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    // Add user with a default role CUSTOMER
    public void saveUser(UserModel user) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            user.setUserRoles(List.of(UserRoles.CUSTOMER)); // default role
        }
        userRepository.save(user);
    }

    // Find user by email
    public Optional<UserModel> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
