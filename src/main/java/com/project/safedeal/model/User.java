package com.project.safedeal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String avatarUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance;


}
