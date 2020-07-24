package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.backend.dto.comment.ContentDto;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    @Override
    public Comment create(Post post, User author, ContentDto dto) {
        Comment entity = new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody(dto.getBody())
                .setPost(post)
                .setAuthor(author);
        return commentRepository.save(entity);
    }
    
    @Override
    public Comment update(Long id, Long authorId, ContentDto dto) {
        Comment entity = findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now())
                .setBody(dto.getBody());
        return commentRepository.save(entity);
    }
    
    @Override
    public void delete(Long id, Long authorId) {
        Comment entity = findByIdAndAuthorId(id, authorId);
        commentRepository.delete(entity);
    }
    
    @Override
    public Page<Comment> findAllByPostId(Long postId, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return commentRepository.findAllByPostId(postId, pageable);
    }
    
    private Comment findByIdAndAuthorId(Long id, Long authorId) {
        return commentRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new NotFoundException("notFound.comment.byIdAndAuthorId", id, authorId));
    }
}
