package com.project.safedeal.repository;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByUserId(Long userId);
    List<Ad> findByUser(User user, Sort sort);
}
