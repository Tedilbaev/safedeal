package com.project.safedeal.controller;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import com.project.safedeal.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping("/all")
    public ResponseEntity<List<Ad>> getAllAds(
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(adService.getAllAds(sortBy, order, title, category));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Ad>> getMyAds(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(adService.getUserAds(authentication, sortBy, order, title));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAdById(@PathVariable Long id) {
        Ad ad = adService.getAdById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));
        return ResponseEntity.ok(ad);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            adService.deleteAd(id, authentication);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAd(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") String price,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) {
        try {
            Ad ad = adService.createAd(authentication, title, description, price, category, location, photo);
            return ResponseEntity.ok(ad);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
//    @PatchMapping("/update")
//    public ResponseEntity<?> updateProfile(
//            Authentication authentication,
//            @RequestParam(value = "title") String title,
//            @RequestParam(value = "description", required = false) String description,
//            @RequestParam(value = "price", required = false) String price,
//            @RequestParam(value = "category", required = false) String category,
//            @RequestParam(value = "location", required = false) String location,
//            @RequestParam(value = "photo", required = false) MultipartFile photo) {
//        try {
//            String avatarUrl = null;
//            if (photo != null && !photo.isEmpty()) {
//                avatarUrl = savePhoto(photo);
//            }
//
//            Ad updatedAd = adService.updateAd(
//                    authentication, title, description, price, category, location, avatarUrl
//            );
//            return ResponseEntity.ok(updatedAd);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Failed to upload avatar: " + e.getMessage());
//        }
//    }

//    private String savePhoto(MultipartFile photo) throws IOException {
//
//        String uploadDir = new File("../userData").getAbsolutePath() + "/";
//        File directory = new File(uploadDir);
//
//        if (!directory.exists()) {
//            boolean created = directory.mkdirs();
//            if (!created) {
//                throw new IOException("Не удалось создать директорию: " + uploadDir);
//            }
//            System.out.println("Директория создана: " + uploadDir);
//        } else {
//            System.out.println("Директория уже существует: " + uploadDir);
//        }
//
//        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
//        File destination = new File(uploadDir + fileName);
//
//        try {
//            photo.transferTo(destination);
//            System.out.println("Файл сохранен: " + destination.getAbsolutePath());
//        } catch (IOException e) {
//            throw new IOException("Ошибка сохранения файла: " + e.getMessage());
//        }
//        return "/userData/" + fileName;
//    }
}
