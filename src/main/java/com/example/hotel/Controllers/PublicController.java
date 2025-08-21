package com.example.hotel.Controllers;

import com.example.hotel.Models.UserModel;
import com.example.hotel.Services.ResetPasswordService;
import com.example.hotel.Services.UserServices;
import com.example.hotel.Utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @GetMapping
    public String show() {
        return "Hey! I am Public controller";
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return "I am running good.";
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserModel user) {
        if (userServices.findUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with this email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userServices.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserModel user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                var roles = authentication.getAuthorities().stream()
                        .map(a -> a.getAuthority()).toList();

                String accessToken = jwtUtil.generateAccessToken(user.getEmail(), roles);
                String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

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

    @PostMapping("/forget-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        return resetPasswordService.createResetToken(email)
                .map(token -> ResponseEntity.ok("Reset token generated: " + token.getToken()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String tokenValue = body.get("token");
        String newPassword = body.get("newPassword");

        if (!resetPasswordService.validateToken(tokenValue)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is invalid or expired");
        }

        boolean success = resetPasswordService.resetPassword(tokenValue, passwordEncoder.encode(newPassword));

        if (success) {
            return ResponseEntity.ok("Password updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to reset password");
        }
    }
}
