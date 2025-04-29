package com.project.safedeal.repository;

import com.project.safedeal.model.Order;
import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAdId(Long AdId);
    List<Order> findByClient(User client, Sort sort);
    List<Order> findByClientAndAdTitleContainingIgnoreCase(User client, String title, Sort sort);
    List<Order> findByAdTitleContainingIgnoreCase(String title, Sort sort);
}