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

    @GetMapping("/my-ads")
    public ResponseEntity<List<Ad>> getUserAds() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        List<Ad> ads = adRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/all-ads")
    public ResponseEntity<List<Ad>> getAllAds() {
        List<Ad> ads = adRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(ads);
    }

    @PostMapping("/create-ad")
    public ResponseEntity<?> createAd(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") String price,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) {
        try {
            if (title == null || title.trim().isEmpty() || price == null || price.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title and price are required");
            }

            User user = userService.getUserFromAuthentication(authentication);

            Ad ad = new Ad();
            ad.setUser(user);
            ad.setTitle(title);
            ad.setDescription(description);
            try {
                ad.setPrice(new BigDecimal(price));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid price format");
            }
            ad.setCategory(category);
            ad.setLocation(location);
            ad.setStatus("ACTIVE");
            ad.setCreatedAt(LocalDateTime.now());
            ad.setUpdatedAt(LocalDateTime.now());

            if (photo != null && !photo.isEmpty()) {
                String photoUrl = savePhoto(photo);
                ad.setPhoto(photoUrl);
            }

            Ad savedAd = adRepository.save(ad);
            return ResponseEntity.ok(savedAd);
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку для диагностики
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    private String savePhoto(MultipartFile photo) throws IOException {
        // Путь к папке userData (на одном уровне с корнем проекта)
        String uploadDir = new File("../userData").getAbsolutePath() + "/";
        File directory = new File(uploadDir);

        // Проверяем и создаём директорию, если её нет
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Не удалось создать директорию: " + uploadDir);
            }
            System.out.println("Директория создана: " + uploadDir);
        } else {
            System.out.println("Директория уже существует: " + uploadDir);
        }

        // Генерируем уникальное имя файла
        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File destination = new File(uploadDir + fileName);

        // Сохраняем файл
        try {
            photo.transferTo(destination);
            System.out.println("Файл сохранен: " + destination.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("Ошибка сохранения файла: " + e.getMessage());
        }

        // Возвращаем относительный путь для использования в фронтенде
        return "/userData/" + fileName;
    }
}