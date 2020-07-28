package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndAuthor(Long id, User author);
    
    Page<Post> findAllByAuthor(User author, Pageable pageable);
}
