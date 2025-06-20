package com.project.safedeal.service;

import com.project.safedeal.model.*;
import com.project.safedeal.repository.AdRepository;
import com.project.safedeal.repository.CommentRepository;
import com.project.safedeal.repository.OrderRepository;
import com.project.safedeal.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final PhotoRepository photoRepository;
    private final CommentRepository commentRepository;

    public Page<Ad> getAllAds(String sortBy, String order, String title, String category, int page, int size) {
        Sort sort = buildSort(sortBy, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (title != null && !title.trim().isEmpty()) {
            return adRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            return adRepository.findByCategoryContainingIgnoreCase(category, pageable);
        }
        return adRepository.findAll(pageable);
    }

    public Page<Ad> getUserAds(Authentication authentication, String sortBy, String order, String title, int page, int size) {
        User user = userService.getUserFromAuthentication(authentication);
        Sort sort = buildSort(sortBy, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (title != null && !title.trim().isEmpty()) {
            return adRepository.findByUserAndTitleContainingIgnoreCase(user, title,pageable);
        }
        return adRepository.findByUser(user, pageable);
    }

    public Ad getAdById(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким id не найден: " + id));
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

    public void deleteAd(Long id, Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));
        if (user.getRole().equals("USER") && !ad.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Вы не можете редактировать это объявление");
        }

        List<Order> orders = orderRepository.findByAdId(id);
        orderRepository.deleteAll(orders);
        List<Photo> photos = photoRepository.findByAdId(id);
        photoRepository.deleteAll(photos);
        List<Comment> comments = commentRepository.findByAdId(id);
        commentRepository.deleteAll(comments);

        adRepository.delete(ad);
    }

    public Ad updateAd(Long id, Authentication authentication, String title, String description,
                       String price, String category, String location, MultipartFile photo) throws IOException {
        User user = userService.getUserFromAuthentication(authentication);
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));

        if (user.getRole().equals("USER") && !ad.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Вы не можете редактировать это объявление");
        }

        if (title != null && !title.trim().isEmpty()) {
            ad.setTitle(title);
        }
        if (description != null) {
            ad.setDescription(description);
        }
        if (price != null && !price.trim().isEmpty()) {
            try {
                ad.setPrice(new BigDecimal(price));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Неверный формат цены");
            }
        }
        if (category != null) {
            ad.setCategory(category);
        }
        if (location != null) {
            ad.setLocation(location);
        }
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = savePhoto(photo);
            ad.setPhoto(photoUrl);
        }

        ad.setUpdatedAt(LocalDateTime.now());
        return adRepository.save(ad);
    }

    public Ad createAd(Authentication authentication, String title, String description, String price,
                       String category, String location, MultipartFile photo) throws IOException {
        if (title == null || title.trim().isEmpty() || price == null || price.trim().isEmpty()) {
            throw new IllegalArgumentException("Название и цена обязательны");
        }

        User user = userService.getUserFromAuthentication(authentication);
        Ad ad = new Ad();
        ad.setUser(user);
        ad.setTitle(title);
        ad.setDescription(description);
        try {
            ad.setPrice(new BigDecimal(price));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат цены");
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
            throw new IOException("FОшибка при создании директории: " + uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File destination = new File(uploadDir + fileName);
        photo.transferTo(destination);
        return "/userData/" + fileName;
    }



}
