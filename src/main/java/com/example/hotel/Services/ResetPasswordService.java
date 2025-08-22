package com.example.hotel.Services;

import com.example.hotel.Models.ResetPasswordModel;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Repositories.ResetPasswordRepository;
import com.example.hotel.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class ResetPasswordService {

    private final ResetPasswordRepository resetPasswordRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public ResetPasswordService(ResetPasswordRepository resetPasswordRepository,
                                UserRepository userRepository,
                                EmailService emailService) {
        this.resetPasswordRepository = resetPasswordRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // Generate secure 256-bit token
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Create or reuse a reset token
    public Optional<ResetPasswordModel> createResetToken(String email) {
        Optional<UserModel> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return Optional.empty();

        UserModel user = userOpt.get();

        // Check existing valid token
        Optional<ResetPasswordModel> existingToken = resetPasswordRepository.findByUserAndUsedFalse(user);
        if (existingToken.isPresent() && existingToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return existingToken;
        }

        // Create new token
        ResetPasswordModel resetToken = new ResetPasswordModel();
        resetToken.setToken(generateSecureToken());
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // expires in 15 minutes

        resetPasswordRepository.save(resetToken);

        // Send email (can be HTML template)
        String body = "Hi " + user.getName() + ",\nUse this token to reset your password: " + resetToken.getToken()
                + "\nThis token will expire in 15 minutes.";
        emailService.sendSimpleEmail(user.getEmail(), "Password Reset Request", body);

        return Optional.of(resetToken);
    }

    // Validate token
    public boolean validateToken(String token) {
        Optional<ResetPasswordModel> tokenOpt = resetPasswordRepository.findByToken(token);
        return tokenOpt.isPresent() &&
                !tokenOpt.get().isUsed() &&
                tokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    // Reset password
    public boolean resetPassword(String token, String encodedNewPassword) {
        Optional<ResetPasswordModel> tokenOpt = resetPasswordRepository.findByToken(token);
        if (tokenOpt.isEmpty()) return false;

        ResetPasswordModel resetToken = tokenOpt.get();
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        UserModel user = resetToken.getUser();
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        resetToken.setUsed(true);
        resetPasswordRepository.save(resetToken);

        return true;
    }

    // Optional: cleanup expired tokens
    public void deleteExpiredTokens() {
        resetPasswordRepository.findAll().stream()
                .filter(t -> t.getExpiryDate().isBefore(LocalDateTime.now()))
                .forEach(resetPasswordRepository::delete);
    }
}
