package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  
  Optional<Comment> findByIdAndAuthor(Long id, User author);
  
  Page<Comment> findAllByPost(Post post, Pageable pageable);
  
}
