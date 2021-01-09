package com.social.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.repository.spring.SpringPostRepository;

@Repository
public class PostRepositoryImpl implements PostRepository {

  private final SpringPostRepository delegate;

  @Autowired
  public PostRepositoryImpl(SpringPostRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public Post save(Post entity) {
    return delegate.save(entity);
  }

  @Override
  public Optional<Post> findById(Long id) {
    return delegate.findById(id);
  }

  @Override
  public Optional<Post> findByIdAndAuthor(Long id, User author) {
    return delegate.findByIdAndAuthor(id, author);
  }

  @Override
  public Page<Post> findAll(Pageable pageable) {
    return delegate.findAll(pageable);
  }

  @Override
  public Page<Post> findAllByAuthor(User author, Pageable pageable) {
    return delegate.findAllByAuthor(author, pageable);
  }

  @Override
  public void delete(Post entity) {
    delegate.delete(entity);
  }

}
