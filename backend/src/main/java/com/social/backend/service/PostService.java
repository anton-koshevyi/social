package com.social.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface PostService {
    Post create(User author, String body);
    
    Post update(Long id, Long authorId, String body);
    
    void delete(Long id, Long authorId);
    
    Post findById(Long id);
    
    Page<Post> findAll(Pageable pageable);
    
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
}
