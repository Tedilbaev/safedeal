package com.project.safedeal.repository;

import com.project.safedeal.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);// Добавь этот метод
    List<User> findByUsernameContainingIgnoreCase(String username, Sort sort);
    List<User> findByEmailContainingIgnoreCase(String email, Sort sort);
    List<User> findAll(Sort sort);
}