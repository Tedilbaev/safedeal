package com.project.safedeal.controller;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.AdRepository;
import com.project.safedeal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdRepository adRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        List<User> users = userService.getAllUsers(sortBy, order, username, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/moder")
    public ResponseEntity<List<User>> getAllModer(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        List<User> moder = userService.getAllModer(sortBy, order, username, email);
        return ResponseEntity.ok(moder);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            String avatarUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                avatarUrl = savePhoto(avatar);
            }

            User updatedUser = userService.updateUserProfile(
                    authentication, username, email, location, description, phone, avatarUrl
            );
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload avatar: " + e.getMessage());
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }
            userService.changePassword(authentication, oldPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String savePhoto(MultipartFile photo) throws IOException {

        String uploadDir = new File("../userData").getAbsolutePath() + "/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Не удалось создать директорию: " + uploadDir);
            }
            System.out.println("Директория создана: " + uploadDir);
        } else {
            System.out.println("Директория уже существует: " + uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File destination = new File(uploadDir + fileName);

        try {
            photo.transferTo(destination);
            System.out.println("Файл сохранен: " + destination.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("Ошибка сохранения файла: " + e.getMessage());
        }
        return "/userData/" + fileName;
    }
}