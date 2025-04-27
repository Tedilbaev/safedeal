package com.project.safedeal.service;

import com.project.safedeal.model.User;
import com.project.safedeal.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUserProfile(Authentication authentication, String username, String email,
                                  String location, String description, String phone, String avatarUrl) {
        User user = getUserFromAuthentication(authentication);

        if (username != null && !username.equals(user.getUsername()) && usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (email != null && !email.equals(user.getEmail()) && emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (location != null) {
            user.setLocation(location);
        }
        if (description != null) {
            user.setDescription(description);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Authentication authentication, String oldPassword, String newPassword) {
        User user = getUserFromAuthentication(authentication);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<User> getAllUsers(String sortBy, String order, String username, String email) {
        Sort sort = buildSort(sortBy, order);
        if (username != null && !username.trim().isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCase(username, sort);
        } else if (email != null && !email.trim().isEmpty()) {
            return userRepository.findByEmailContainingIgnoreCase(email, sort);
        }
        return userRepository.findAll(sort);
    }

    private Sort buildSort(String sortBy, String order) {
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "id") {
            case "username" -> "username";
            case "email" -> "email";
            case "createdat" -> "createdAt";
            default -> "id";
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}