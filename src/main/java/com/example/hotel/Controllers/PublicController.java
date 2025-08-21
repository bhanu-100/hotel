package com.example.hotel.Controllers;

import com.example.hotel.Models.UserModel;
import com.example.hotel.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserServices userServices;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "I am running good.";
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserModel user) {
        // Check if user already exists
        Optional<UserModel> existingUser = userServices.findUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with this email already exists!");
        }

        // Add new user
        userServices.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully!");
    }
}
