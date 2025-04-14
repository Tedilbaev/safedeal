package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserService userService;

    public List<Ad> getAllAds(String sortBy, String order) {
        Sort sort = buildSort(sortBy, order);
        return adRepository.findAll(sort);
    }

    public List<Ad> getUserAds(Authentication authentication, String sortBy, String order) {
        User user = userService.getUserFromAuthentication(authentication);
        Sort sort = buildSort(sortBy, order);
        return adRepository.findByUser(user, sort);
    }

    private Sort buildSort(String sortBy, String order) {
        // Допустимые поля для сортировки
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "createdAt") {
            case "price" -> "price";
            case "title" -> "title";
            case "createdAt" -> "createdAt";
            default -> "createdAt"; // По умолчанию
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    public Ad createAd(Authentication authentication, String title, String description, String price,
                       String category, String location, MultipartFile photo) throws IOException {
        if (title == null || title.trim().isEmpty() || price == null || price.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and price are required");
        }

        User user = userService.getUserFromAuthentication(authentication);
        Ad ad = new Ad();
        ad.setUser(user);
        ad.setTitle(title);
        ad.setDescription(description);
        try {
            ad.setPrice(new BigDecimal(price));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format");
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

        return adRepository.save(ad);
    }

    private String savePhoto(MultipartFile photo) throws IOException {
        String uploadDir = new File("../userData").getAbsolutePath() + "/";
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File destination = new File(uploadDir + fileName);
        photo.transferTo(destination);
        return "/userData/" + fileName;
    }

}
