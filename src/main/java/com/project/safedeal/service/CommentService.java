package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Comment;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final AdService adService;

    public List<Comment> getAllCommentByAd(Authentication authentication, String sortBy, String order, Long adId) {
        Ad ad = adService.getAdById(adId);
        Sort sort = buildSort(sortBy, order);
        return commentRepository.findByAd(ad, sort);
    }

    private Sort buildSort(String sortBy, String order) {
        // Допустимые поля для сортировки
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "createdAt") {
            case "textComment" -> "textComment";
            case "createdAt" -> "createdAt";
            default -> "createdAt"; // По умолчанию
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    public Comment createComment(Authentication authentication, Long adId, String textComment) throws IOException {
        if (textComment == null || textComment.trim().isEmpty()) {
            throw new IllegalArgumentException("Комментарий обязательно");
        }
        User user = userService.getUserFromAuthentication(authentication);
        Ad ad = adService.getAdById(adId);
        Comment comment = new Comment();
        comment.setTextComment(textComment);
        comment.setAd(ad);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Комментарий не найден"));
        if (user != comment.getUser()) {
            throw new IllegalArgumentException("Вы не можете удалять чужие комментарии");
        }
        commentRepository.delete(comment);
    }

}