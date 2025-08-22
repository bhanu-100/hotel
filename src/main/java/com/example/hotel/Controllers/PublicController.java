package com.example.hotel.Controllers;

import com.example.hotel.DTOs.LoginDTO;
import com.example.hotel.DTOs.PasswordResetDTO;
import com.example.hotel.DTOs.PasswordResetRequestDTO;
import com.example.hotel.DTOs.UserRequestDTO;
import com.example.hotel.Enums.UserRoles;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Services.ResetPasswordService;
import com.example.hotel.Services.UserService;
import com.example.hotel.Utilities.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "User Operations", description = "APIs for user management and authentication")
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Operation(summary = "Health check", description = "Check if the service is running")
    @GetMapping("/health-check")
    public String healthCheck() {
        return "Service is running!";
    }

    @Operation(summary = "Sign up a new user", description = "Create a new user account")
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        if (userService.findUserByEmail(userRequestDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with this email already exists!");
        }

        UserModel user = new UserModel();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setUserRoles(userRequestDTO.getRoles() != null ?
                userRequestDTO.getRoles() :
                Collections.singletonList(UserRoles.CUSTOMER));
        user.setActive(true);

        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully!");
    }

    @Operation(summary = "Login user", description = "Authenticate user and return JWT access & refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            if (authentication.isAuthenticated()) {
                var roles = authentication.getAuthorities().stream()
                        .map(a -> a.getAuthority()).toList();

                String accessToken = jwtUtil.generateAccessToken(loginDTO.getEmail(), roles);
                String refreshToken = jwtUtil.generateRefreshToken(loginDTO.getEmail());

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);

                return ResponseEntity.ok(tokens);
            } else {
                throw new BadCredentialsException("Invalid email or password");
            }
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password!");
        }
    }

    @Operation(summary = "Request password reset", description = "Generate a password reset token for a user")
    @PostMapping("/forget-password")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        return resetPasswordService.createResetToken(requestDTO.getEmail())
                .map(token -> ResponseEntity.ok("Reset token generated: " + token.getToken()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found"));
    }

    @Operation(summary = "Reset password", description = "Reset user's password using token")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO resetDTO) {
        if (!resetPasswordService.validateToken(resetDTO.getToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is invalid or expired");
        }

        boolean success = resetPasswordService.resetPassword(
                resetDTO.getToken(),
                passwordEncoder.encode(resetDTO.getNewPassword())
        );

        if (success) {
            return ResponseEntity.ok("Password updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to reset password");
        }
    }
}
