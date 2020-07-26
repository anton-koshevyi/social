package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepository;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    @Override
    public Comment create(Post post, User author, String body) {
        User postAuthor = post.getAuthor();
    
        if (postAuthor.isPrivate()) {
            if (!Objects.equals(postAuthor.getId(), author.getId())) {
                throw new IllegalActionException("illegalAction.comment.privatePost");
            }
        }
    
        if (postAuthor.isInternal()) {
            if (!postAuthor.hasFriendship(author)) {
                throw new IllegalActionException("illegalAction.comment.internalPost");
            }
        }
    
        Comment entity = new Comment();
        entity.setBody(body);
        entity.setPost(post);
        entity.setAuthor(author);
        return commentRepository.save(entity);
    }
    
    @Override
    public Comment update(Long id, Long authorId, String body) {
        Comment entity = findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now());
        entity.setBody(body);
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
