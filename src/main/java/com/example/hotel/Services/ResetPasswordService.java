package com.example.hotel.Services;

import com.example.hotel.Models.ResetPasswordModel;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Repositories.ResetPasswordRepository;
import com.example.hotel.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class ResetPasswordService {

    private final ResetPasswordRepository resetPasswordRepository;
    private final UserRepository userRepository;

    public ResetPasswordService(ResetPasswordRepository resetPasswordRepository,
                                UserRepository userRepository) {
        this.resetPasswordRepository = resetPasswordRepository;
        this.userRepository = userRepository;
    }

    // Generate a secure token
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256-bit token
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Create or reuse a reset token for a user
    public Optional<ResetPasswordModel> createResetToken(String email) {
        Optional<UserModel> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return Optional.empty();

        UserModel user = userOpt.get();

        // Check if there is already an active token
        Optional<ResetPasswordModel> existingToken = resetPasswordRepository.findByUserAndUsedFalse(user);
        if (existingToken.isPresent()) {
            return existingToken; // Return existing token
        }

        // Otherwise, create a new token
        ResetPasswordModel resetToken = new ResetPasswordModel();
        resetToken.setToken(generateSecureToken());
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 15 * 60 * 1000)); // 15 min expiry

        resetPasswordRepository.save(resetToken);
        return Optional.of(resetToken);
    }

    // Find token by value
    public Optional<ResetPasswordModel> findToken(String token) {
        return resetPasswordRepository.findByToken(token);
    }

    // Validate token
    public boolean validateToken(String token) {
        Optional<ResetPasswordModel> tokenOpt = resetPasswordRepository.findByToken(token);
        if (tokenOpt.isEmpty()) return false;

        ResetPasswordModel resetToken = tokenOpt.get();
        return !resetToken.isUsed() && resetToken.getExpiryDate().after(new Date());
    }

    // Reset password
    public boolean resetPassword(String token, String encodedNewPassword) {
        Optional<ResetPasswordModel> tokenOpt = resetPasswordRepository.findByToken(token);
        if (tokenOpt.isEmpty()) return false;

        ResetPasswordModel resetToken = tokenOpt.get();
        if (resetToken.isUsed() || resetToken.getExpiryDate().before(new Date())) return false;

        // Update user password
        UserModel user = resetToken.getUser();
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        resetPasswordRepository.save(resetToken);

        return true;
    }
}
