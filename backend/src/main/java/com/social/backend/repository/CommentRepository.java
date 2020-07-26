package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.post.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);
    
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);
}
