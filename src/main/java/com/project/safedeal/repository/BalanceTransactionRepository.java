package com.project.safedeal.repository;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.BalanceTransaction;
import com.project.safedeal.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceTransactionRepository extends JpaRepository<BalanceTransaction, Long> {
    List<BalanceTransaction> findByBalanceTransactionId(Long BalanceTransactionId);
    List<BalanceTransaction> findByUser(User user, Sort sort);
    List<BalanceTransaction> findByUserAndTypeContainingIgnoreCase(User user, String type, Sort sort);
}
