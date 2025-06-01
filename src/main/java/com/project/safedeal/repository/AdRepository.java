package com.project.safedeal.repository;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByUserId(Long userId);
    Page<Ad> findByUser(User user, Pageable pageable);
    Page<Ad> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Ad> findByCategoryContainingIgnoreCase(String category, Pageable pageable);
    Page<Ad> findByUserAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);
    Page<Ad> findAll(Pageable pageable);
}
