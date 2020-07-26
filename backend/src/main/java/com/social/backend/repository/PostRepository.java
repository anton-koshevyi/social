package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndAuthorId(Long id, Long authorId);
    
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
}
