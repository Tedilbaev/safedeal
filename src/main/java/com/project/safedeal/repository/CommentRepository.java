package com.project.safedeal.repository;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Comment;
import com.project.safedeal.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAdContainingIgnoreCase(Ad ad, Sort sort);
    List<Comment> findAll(Sort sort);
}

