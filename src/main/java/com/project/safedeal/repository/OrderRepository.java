package com.project.safedeal.repository;

import com.project.safedeal.model.Order;
import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAdId(Long AdId);
    Page<Order> findByClient(User client, Pageable pageable);
    Page<Order> findByClientAndAdTitleContainingIgnoreCase(User client, String title, Pageable pageable);
    Page<Order> findByAdTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
}