package com.example.hotel.Services;

import com.example.hotel.DTOs.UserRequestDTO;
import com.example.hotel.DTOs.UserResponseDTO;
import com.example.hotel.Enums.UserRoles;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Create a new user using DTO
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserRoles(dto.getRoles() != null ? dto.getRoles() : List.of(UserRoles.CUSTOMER));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        UserModel saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    // ✅ Update existing user
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserRoles(dto.getRoles() != null ? dto.getRoles() : user.getUserRoles());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());

        UserModel updated = userRepository.save(user);
        return mapToDTO(updated);
    }

    // ✅ Get all users
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get user by ID
    public UserResponseDTO getUserById(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    // ✅ Delete user
    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // ✅ Find user by email (needed in PublicController)
    public Optional<UserModel> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ Save UserModel directly (needed in PublicController signUp)
    public UserModel saveUser(UserModel user) {
        return userRepository.save(user);
    }

    // Mapper: UserModel -> UserResponseDTO
    private UserResponseDTO mapToDTO(UserModel user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getUserRoles());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
