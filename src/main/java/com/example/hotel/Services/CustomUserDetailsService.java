package com.example.hotel.Services;

import com.example.hotel.Models.UserModel;
import com.example.hotel.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("❌ User not found with email: " + email));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())   // make sure this is already encoded with BCrypt
                .authorities(
                        user.getUserRoles().stream()
                                .map(role -> "ROLE_" + role.name()) // Example: ROLE_ADMIN, ROLE_USER
                                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
