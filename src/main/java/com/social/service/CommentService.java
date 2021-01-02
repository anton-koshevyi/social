package com.social.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;

public interface CommentService {
  
  Comment create(Post post, User author, String body);
  
  Comment update(Long id, User author, String body);
  
  void delete(Long id, User author);
  
  Page<Comment> findAll(Post post, Pageable pageable);
  
}
