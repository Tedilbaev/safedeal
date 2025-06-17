package com.project.safedeal.controller;

import com.project.safedeal.model.Comment;
import com.project.safedeal.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getAllComments(
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam("adId") Long adId) {
        return ResponseEntity.ok(commentService.getAllCommentByAd(sortBy, order, adId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            commentService.deleteComment(id, authentication);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComment(
            @RequestParam(value = "textComment", required = false) String textComment,
            @RequestParam("adId") Long adId,
            Authentication authentication) {
        try {
            Comment comment = commentService.createComment(authentication, adId, textComment);
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}