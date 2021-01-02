package com.social.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.repository.jpa.CommentJpaRepository;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

  private final CommentJpaRepository delegate;

  @Autowired
  public CommentRepositoryImpl(CommentJpaRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public Comment save(Comment entity) {
    return delegate.save(entity);
  }

  @Override
  public Optional<Comment> findByIdAndAuthor(Long id, User author) {
    return delegate.findByIdAndAuthor(id, author);
  }

  @Override
  public Page<Comment> findAllByPost(Post post, Pageable pageable) {
    return delegate.findAllByPost(post, pageable);
  }

  @Override
  public void delete(Comment entity) {
    delegate.delete(entity);
  }

}
