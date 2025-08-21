package com.example.hotel.Services;

import com.example.hotel.Enums.UserRoles;
import com.example.hotel.Models.UserModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    private final List<UserModel> userModelList = new ArrayList<>();

    // Add user with a default role CUSTOMER
    public void addUser(UserModel user) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            user.setUserRoles(List.of(UserRoles.CUSTOMER)); // default role
        }
        userModelList.add(user);
    }

    // Find user by email
    public Optional<UserModel> findUser(String email) {
        return userModelList.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
