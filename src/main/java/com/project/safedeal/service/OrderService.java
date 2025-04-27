package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Order;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.AdRepository;
import com.project.safedeal.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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


}