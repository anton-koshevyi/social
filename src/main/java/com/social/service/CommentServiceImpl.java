package com.social.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.exception.IllegalActionException;
import com.social.exception.NotFoundException;
import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.repository.CommentRepository;
import com.social.util.NullableUtils;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  @Autowired
  public CommentServiceImpl(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Transactional
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
    return commentRepository.save(entity);
  }

  @Transactional
  @Override
  public Comment update(Long id, User author, String body) {
    Comment entity = findByIdAndAuthor(id, author);
    entity.setUpdatedAt(ZonedDateTime.now());
    NullableUtils.set(entity::setBody, body);
    return commentRepository.save(entity);
  }

  @Transactional
  @Override
  public void delete(Long id, User author) {
    Comment entity = findByIdAndAuthor(id, author);
    commentRepository.delete(entity);
  }

  @Override
  public Page<Comment> findAll(Post post, Pageable pageable) {
    return commentRepository.findAllByPost(post, pageable);
  }

  private Comment findByIdAndAuthor(Long id, User author) {
    return commentRepository.findByIdAndAuthor(id, author)
        .orElseThrow(() -> new NotFoundException(
            "notFound.comment.byIdAndAuthor", id, author.getId()));
  }

}
