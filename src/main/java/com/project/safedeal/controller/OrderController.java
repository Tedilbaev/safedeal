package com.project.safedeal.controller;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Order;
import com.project.safedeal.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createAd(
            @RequestParam("adId") Long adId,
            @RequestParam("performerId") Long performerId,
            Authentication authentication) {
        try {
            Order order = orderService.createOrder(authentication, adId, performerId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<Page<Order>> getMyOrders(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(orderService.getUserOrders(authentication, sortBy, order, title, page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Order>> getAllOrders(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(orderService.getAllOrders(authentication, sortBy, order, title, page, size));
    }
}