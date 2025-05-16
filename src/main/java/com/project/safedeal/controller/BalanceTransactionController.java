package com.project.safedeal.controller;

import com.project.safedeal.model.BalanceTransaction;
import com.project.safedeal.service.BalanceTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceTransactionController {

    private final BalanceTransactionService balanceTransactionService;

    // Получение истории операций пользователя
    @GetMapping("/transactions")
    public ResponseEntity<List<BalanceTransaction>> getUserTransactions(
            Authentication authentication,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String type) {
        List<BalanceTransaction> transactions = balanceTransactionService.getUserTransaction(authentication, sortBy, order, type);
        return ResponseEntity.ok(transactions);
    }

    // Пополнение баланса
    @PostMapping("/replenish")
    public ResponseEntity<BalanceTransaction> replenishBalance(
            Authentication authentication,
            @RequestParam("userId") Long userId,
            @RequestParam("amount") String amount) {
        BalanceTransaction transaction = balanceTransactionService.replenishment(authentication, userId, amount);
        return ResponseEntity.ok(transaction);
    }

    // Списание средств
    @PostMapping("/withdraw")
    public ResponseEntity<BalanceTransaction> withdrawBalance(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {
        BalanceTransaction transaction = balanceTransactionService.withdraw(userId, amount);
        return ResponseEntity.ok(transaction);
    }

    // Оплата заказа
    @PostMapping("/payment")
    public ResponseEntity<BalanceTransaction> makePayment(
            @RequestParam Long userId,
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount) {
        BalanceTransaction transaction = balanceTransactionService.payment(userId, orderId, amount);
        return ResponseEntity.ok(transaction);
    }

    // Зачисление средств за заказ
    @PostMapping("/deposit")
    public ResponseEntity<BalanceTransaction> depositForOrder(
            @RequestParam Long userId,
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount) {
        BalanceTransaction transaction = balanceTransactionService.deposit(userId, orderId, amount);
        return ResponseEntity.ok(transaction);
    }
}
