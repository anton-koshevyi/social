package com.social.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface CommentService {
    Comment create(Post post, User author, String body);
    
    Comment update(Long id, User author, String body);
    
    void delete(Long id, User author);
    
    Page<Comment> findAllByPost(Post post, Pageable pageable);
}
