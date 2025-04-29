package com.project.safedeal.controller;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Order;
import com.project.safedeal.model.Photo;
import com.project.safedeal.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/create")
    public ResponseEntity<?> createAd(
            @RequestParam("adId") Long adId,
            @RequestParam(value = "photo", required = false) MultipartFile[] photoFiles,
            Authentication authentication) {
        try {
            List<Photo> photos = photoService.createPhoto(authentication, adId, photoFiles);
            return ResponseEntity.ok(photos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/adPhotos")
    public ResponseEntity<List<Photo>> getAdPhotos(
            Authentication authentication,
            @RequestParam("adId") Long adId) {
        return ResponseEntity.ok(photoService.getAdPhotos(authentication, adId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            photoService.deletePhoto(id, authentication);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

}
