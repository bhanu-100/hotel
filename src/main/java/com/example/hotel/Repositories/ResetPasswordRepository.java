package com.example.hotel.Repositories;

import com.example.hotel.Models.ResetPasswordModel;
import com.example.hotel.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordRepository extends JpaRepository<ResetPasswordModel, Long> {

    // Find token by token string
    Optional<ResetPasswordModel> findByToken(String token);

    // Find active (unused) token for a specific user
    Optional<ResetPasswordModel> findByUserAndUsedFalse(UserModel user);
}
