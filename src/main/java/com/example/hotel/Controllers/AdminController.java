package com.example.hotel.Controllers;

import com.example.hotel.DTOs.UserRequestDTO;
import com.example.hotel.DTOs.UserResponseDTO;
import com.example.hotel.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Controller", description = "Operations available for admin users")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/check")
    @Operation(summary = "Health check for Admin Controller", description = "Returns a simple message to check if admin controller is working")
    public String show() {
        return "Hey! I am Admin controller";
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Fetch a list of all users in the system")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Fetch details of a specific user by their ID")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user", description = "Admin can create a new user by providing user details")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's information")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
