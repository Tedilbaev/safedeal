package com.project.safedeal.repository;

import com.project.safedeal.model.Category;
import com.project.safedeal.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name, Sort sort);
    List<Category> findAll(Sort sort);
}
