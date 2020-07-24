package com.social.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.dto.post.ContentDto;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface PostService {
    Post create(User author, ContentDto dto);
    
    Post update(Long id, Long authorId, ContentDto dto);
    
    void delete(Long id, Long authorId);
    
    Post findById(Long id);
    
    Post findByIdAndAuthorId(Long id, Long authorId);
    
    Page<Post> findAll(Pageable pageable);
    
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
}
