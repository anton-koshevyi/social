package com.social.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.post.Post;
import com.social.model.user.User;

public interface PostService {
  
  Post create(User author, String title, String body);
  
  Post update(Long id, User author, String title, String body);
  
  void delete(Long id, User author);
  
  Post find(Long id);
  
  Page<Post> findAll(Pageable pageable);
  
  Page<Post> findAll(User author, Pageable pageable);
  
}
