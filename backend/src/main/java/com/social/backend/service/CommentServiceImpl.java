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
  public Comment update(Long id, User author, String body) {
    Comment entity = findByIdAndAuthor(id, author);
    entity.setUpdated(ZonedDateTime.now());
    entity.setBody(body);
    return repository.save(entity);
  }
  
  @Override
  public void delete(Long id, User author) {
    Comment entity = findByIdAndAuthor(id, author);
    repository.delete(entity);
  }
  
  @Override
  public Page<Comment> findAll(Post post, Pageable pageable) {
    return repository.findAllByPost(post, pageable);
  }
  
  private Comment findByIdAndAuthor(Long id, User author) {
    return repository.findByIdAndAuthor(id, author)
        .orElseThrow(() -> new NotFoundException(
            "notFound.comment.byIdAndAuthor", id, author.getId()));
  }
  
}
