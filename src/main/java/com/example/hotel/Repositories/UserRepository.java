package com.example.hotel.Repositories;

import com.example.hotel.Models.UserModel;
import com.example.hotel.Enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    // Find user by email
    Optional<UserModel> findByEmail(String email);

    // Check if user exists by email
    boolean existsByEmail(String email);

}
