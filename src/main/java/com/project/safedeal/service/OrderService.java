package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Order;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.AdRepository;
import com.project.safedeal.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        order.setStatus("Активно");
        ad.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Page<Order> getUserOrders(Authentication authentication, String sortBy, String order, String title, int page, int size) {
        User user = userService.getUserFromAuthentication(authentication);
        Sort sort = buildSort(sortBy, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (title != null && !title.trim().isEmpty()) {
            return orderRepository.findByClientAndAdTitleContainingIgnoreCase(user, title, pageable);
        }
        return orderRepository.findByClient(user, pageable);
    }

    public Page<Order> getAllOrders(Authentication authentication, String sortBy, String order, String title, int page, int size) {
        Sort sort = buildSort(sortBy, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (title != null && !title.trim().isEmpty()) {
            return orderRepository.findByAdTitleContainingIgnoreCase(title, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    private Sort buildSort(String sortBy, String order) {
        Sort.Direction direction =
                "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (sortBy == null) {
            sortBy = "createdAt";
        }

        return switch (sortBy.toLowerCase()) {

            case "id", "status", "createdat", "endat" -> Sort.by(direction, sortBy);

            case "title" -> JpaSort.unsafe(direction, "ad.title");
            case "price" -> JpaSort.unsafe(direction, "ad.price");
            case "category" -> JpaSort.unsafe(direction, "ad.category");

            default -> Sort.by(direction, "createdAt");
        };
    }

}