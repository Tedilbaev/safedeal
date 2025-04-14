package com.project.safedeal.controller;

import com.project.safedeal.model.Ad;
import com.project.safedeal.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping("/all")
    public ResponseEntity<List<Ad>> getAllAds(
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        return ResponseEntity.ok(adService.getAllAds(sortBy, order));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Ad>> getMyAds(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        return ResponseEntity.ok(adService.getUserAds(authentication, sortBy, order));
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
}
