package com.social.repository.spring;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;

public interface CommentRepositorySpring extends JpaRepository<Comment, Long> {

  Optional<Comment> findByIdAndAuthor(Long id, User author);

  Page<Comment> findAllByPost(Post post, Pageable pageable);

}
