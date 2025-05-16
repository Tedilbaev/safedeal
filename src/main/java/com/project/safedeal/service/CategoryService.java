package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Category;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public List<Category> getAllCategory(String sortBy, String order, String name) {
        Sort sort = buildSort(sortBy, order);
        if (name != null && !name.trim().isEmpty()) {
            return categoryRepository.findByNameContainingIgnoreCase(name, sort);
        }
        return categoryRepository.findAll(sort);
    }

    private Sort buildSort(String sortBy, String order) {
        // Допустимые поля для сортировки
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "createdAt") {
            case "name" -> "name";
            case "createdAt" -> "createdAt";
            default -> "createdAt"; // По умолчанию
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    public Category createCategory(Authentication authentication, String name) throws IOException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название обязательно");
        }
        Category category = new Category();
        category.setName(name);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id, Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));
        if (!user.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("Вы не можете редактировать это объявление");
        }
        categoryRepository.delete(category);
    }

}