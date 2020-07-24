package com.social.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.dto.comment.ContentDto;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface CommentService {
    Comment create(Post post, User author, ContentDto dto);
    
    Comment update(Long id, Long authorId, ContentDto dto);
    
    void delete(Long id, Long authorId);
    
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);
}
