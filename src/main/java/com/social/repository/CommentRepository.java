package com.social.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;

public interface CommentRepository {

  Comment save(Comment entity);

  Optional<Comment> findByIdAndAuthor(Long id, User author);

  Page<Comment> findAllByPost(Post post, Pageable pageable);

  void delete(Comment entity);

}
