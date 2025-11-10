package com.example.hotel.Controllers;

import com.example.hotel.DTOs.LoginDTO;
import com.example.hotel.DTOs.PasswordResetDTO;
import com.example.hotel.DTOs.PasswordResetRequestDTO;
import com.example.hotel.DTOs.UserRequestDTO;
import com.example.hotel.Enums.UserRoles;
import com.example.hotel.Models.ResetPasswordModel;
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
import java.util.Optional;

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
                        .map(a -> a.getAuthority())
                        .toList();

                String accessToken = jwtUtil.generateAccessToken(loginDTO.getEmail(), roles);
                String refreshToken = jwtUtil.generateRefreshToken(loginDTO.getEmail());

                Map<String, Object> tokens = new HashMap<>();
                tokens.put("status", "success");
                tokens.put("message", "Login successful");
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                tokens.put("expiresIn", 1000 * 60 * 15);

                return ResponseEntity.ok(tokens);
            } else {
                throw new BadCredentialsException("Invalid email or password");
            }

        } catch (BadCredentialsException ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Request password reset", description = "Generate a password reset token for a user")
    @PostMapping("/forget-password")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        return resetPasswordService.createResetToken(requestDTO.getEmail())
                .map(token -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Reset token generated and sent successfully to email!");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "User not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }


    @Operation(summary = "Reset password", description = "Reset user's password using token")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO resetDTO) {
        Map<String, Object> response = new HashMap<>();

        if (!resetPasswordService.validateToken(resetDTO.getToken())) {
            response.put("status", "error");
            response.put("message", "Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean success = resetPasswordService.resetPassword(
                resetDTO.getToken(),
                passwordEncoder.encode(resetDTO.getNewPassword())
        );

        if (success) {
            response.put("status", "success");
            response.put("message", "Password updated successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to reset password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
