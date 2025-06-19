package com.project.safedeal.repository;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAd(Ad ad, Sort sort);
    List<Comment> findAll(Sort sort);
    List<Comment> findByAdId(Long AdId);
}

