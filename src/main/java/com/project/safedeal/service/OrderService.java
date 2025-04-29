package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Order;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.AdRepository;
import com.project.safedeal.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
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
public class OrderService {

    private final UserService userService;
    private final AdService adService;
    private final OrderRepository orderRepository;

    public Order createOrder(Authentication authentication, Long adId, Long performerId) throws IOException {
        User user = userService.getUserFromAuthentication(authentication);
        User performer = userService.getUserById(performerId);
        Ad ad = adService.getAdById(adId);
        Order order = new Order();
        order.setClient(user);
        order.setPerformer(performer);
        order.setAd(ad);
        order.setStatus("ACTIVE");
        ad.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Authentication authentication, String sortBy, String order, String title) {
        User user = userService.getUserFromAuthentication(authentication);
        Sort sort = buildSort(sortBy, order);
        if (title != null && !title.trim().isEmpty()) {
            return orderRepository.findByClientAndAdTitleContainingIgnoreCase(user, title, sort);
        }
        return orderRepository.findByClient(user, sort);
    }

    public List<Order> getAllOrders(Authentication authentication, String sortBy, String order, String title) {
        Sort sort = buildSort(sortBy, order);
        if (title != null && !title.trim().isEmpty()) {
            return orderRepository.findByAdTitleContainingIgnoreCase(title, sort);
        }
        return orderRepository.findAll(sort);
    }

    private Sort buildSort(String sortBy, String order) {
        // Определяем направление сортировки
        Sort.Direction direction =
                "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Обрабатываем поля Order и связанных сущностей (Ad)
        if (sortBy == null) {
            sortBy = "createdAt"; // Сортировка по умолчанию
        }

        return switch (sortBy.toLowerCase()) {
            // Поля из Order
            case "id", "status", "createdat", "endat" -> Sort.by(direction, sortBy);

            // Поля из Ad (сортировка по связанной сущности)
            case "title" -> JpaSort.unsafe(direction, "ad.title"); // Сортировка по ad.title
            case "price" -> JpaSort.unsafe(direction, "ad.price"); // Сортировка по ad.price
            case "category" -> JpaSort.unsafe(direction, "ad.category"); // Сортировка по ad.category

            // Если поле не найдено — сортируем по дате создания (createdAt)
            default -> Sort.by(direction, "createdAt");
        };
    }

}