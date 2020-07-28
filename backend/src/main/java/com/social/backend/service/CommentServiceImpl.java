package com.social.backend.service;

import java.time.ZonedDateTime;

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
    private final CommentRepository repository;
    
    @Autowired
    public CommentServiceImpl(CommentRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Comment create(Post post, User author, String body) {
        User postAuthor = post.getAuthor();
    
        if (postAuthor.isPrivate() && !postAuthor.equals(author)) {
            throw new IllegalActionException("illegalAction.comment.privatePost");
        }
    
        if (postAuthor.isInternal() && !postAuthor.hasFriendship(author)) {
            throw new IllegalActionException("illegalAction.comment.internalPost");
        }
    
        Comment entity = new Comment();
        entity.setBody(body);
        entity.setPost(post);
        entity.setAuthor(author);
        return repository.save(entity);
    }
    
    @Override
    public Comment update(Long id, Long authorId, String body) {
        Comment entity = findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now());
        entity.setBody(body);
        return repository.save(entity);
    }
    
    @Override
    public void delete(Long id, Long authorId) {
        Comment entity = findByIdAndAuthorId(id, authorId);
        repository.delete(entity);
    }
    
    @Override
    public Page<Comment> findAllByPostId(Long postId, Pageable pageable) {
        return repository.findAllByPostId(postId, pageable);
    }
    
    private Comment findByIdAndAuthorId(Long id, Long authorId) {
        return repository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new NotFoundException("notFound.comment.byIdAndAuthorId", id, authorId));
    }
}
