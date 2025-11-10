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

        // ✅ Check if a valid token already exists
        Optional<ResetPasswordModel> existingToken = resetPasswordRepository.findByUserAndUsedFalse(user);
        if (existingToken.isPresent() && existingToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return existingToken;
        }

        // ✅ Create a new token
        ResetPasswordModel resetToken = new ResetPasswordModel();
        resetToken.setToken(generateSecureToken());
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // expires in 15 minutes
        resetPasswordRepository.save(resetToken);

        // ✅ Construct the reset link (frontend route)
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken.getToken();

        // ✅ HTML Email Body (clean, styled, user-friendly)
        String body = """
        <div style="font-family: Arial, sans-serif; color: #333;">
            <h2>Password Reset Request</h2>
            <p>Hi %s,</p>
            <p>We received a request to reset your password for your account.</p>
            <p>Click the link below to reset your password:</p>
            <a href="%s" 
               style="display:inline-block; background-color:#007bff; color:white; padding:10px 20px; 
                      text-decoration:none; border-radius:5px;">Reset Password</a>
            <p style="margin-top:20px;">This link will expire in <strong>15 minutes</strong>.</p>
            <p>If you didn’t request this, you can safely ignore this email.</p>
            <hr>
            <p style="font-size:12px; color:#777;">This is an automated message, please do not reply.</p>
        </div>
        """.formatted(user.getName(), resetLink);

        // ✅ Send email (HTML format)
        emailService.sendHtmlEmail(user.getEmail(), "Password Reset Request", body);

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
