package com.example.hotel.Controllers;

import com.example.hotel.Models.UserModel;
import com.example.hotel.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    // Login API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserModel loginRequest) {
        Optional<UserModel> userOptional = userServices.findUserByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user);  // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }
}
