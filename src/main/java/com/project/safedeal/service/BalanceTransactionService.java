package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.BalanceTransaction;
import com.project.safedeal.model.Order;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.BalanceTransactionRepository;
import com.project.safedeal.repository.OrderRepository;
import com.project.safedeal.repository.UserRepository;
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
public class BalanceTransactionService {

    private final BalanceTransactionRepository balanceTransactionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;


    public List<BalanceTransaction> getUserTransaction(Authentication authentication, String sortBy, String order, String type) {
        User user = userService.getUserFromAuthentication(authentication);
        Sort sort = buildSort(sortBy, order);
        if (type != null && !type.trim().isEmpty()) {
            return balanceTransactionRepository.findByUserAndTypeContainingIgnoreCase(user, type, sort);
        }
        return balanceTransactionRepository.findByUser(user, sort);
    }

    private Sort buildSort(String sortBy, String order) {

        String field = switch (sortBy != null ? sortBy.toLowerCase() : "createdAt") {
            case "amount" -> "amount";
            case "type" -> "type";
            case "createdAt" -> "createdAt";
            default -> "createdAt";
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    @Transactional
    public BalanceTransaction replenishment(Authentication authentication, Long userId, String amount, String cardNumber) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setBalance(user.getBalance().add(new BigDecimal(amount)));
        BalanceTransaction transaction = new BalanceTransaction();
        transaction.setUser(user);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setType("Пополнение");
        transaction.setCardNumber(cardNumber);
        balanceTransactionRepository.save(transaction);
        userRepository.save(user);
        return transaction;
    }

    @Transactional
    public BalanceTransaction withdraw(Long userId, BigDecimal amount, String cardNumber) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        user.setBalance(user.getBalance().subtract(amount));
        BalanceTransaction transaction = new BalanceTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType("Списание");
        transaction.setCardNumber(cardNumber);
        balanceTransactionRepository.save(transaction);
        userRepository.save(user);
        return transaction;
    }

    @Transactional
    public BalanceTransaction payment(Long userId, Long orderId, BigDecimal amount, String cardNumber) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Заказ не найден"));
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        user.setBalance(user.getBalance().subtract(amount));
        BalanceTransaction transaction = new BalanceTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType("Оплата заказа");
        transaction.setOrder(order);
        transaction.setCardNumber(cardNumber);
        balanceTransactionRepository.save(transaction);
        userRepository.save(user);
        return transaction;
    }

    @Transactional
    public BalanceTransaction deposit(Long userId, Long orderId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Заказ не найден"));
        user.setBalance(user.getBalance().add(amount));
        BalanceTransaction transaction = new BalanceTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType("Зачисление за заказ");
        transaction.setOrder(order);
        balanceTransactionRepository.save(transaction);
        userRepository.save(user);
        return transaction;
    }
}