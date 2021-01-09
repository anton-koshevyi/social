package com.social.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.social.model.user.User;
import com.social.repository.spring.SpringUserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final SpringUserRepository delegate;

  @Autowired
  public UserRepositoryImpl(SpringUserRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public User save(User entity) {
    return delegate.save(entity);
  }

  @Override
  public Optional<User> findById(Long id) {
    return delegate.findById(id);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return delegate.findByEmail(email);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return delegate.findByUsername(username);
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return delegate.findAll(pageable);
  }

  @Override
  public boolean existsByEmail(String email) {
    return delegate.existsByEmail(email);
  }

  @Override
  public boolean existsByUsername(String username) {
    return delegate.existsByUsername(username);
  }

  @Override
  public void delete(User entity) {
    delegate.delete(entity);
  }

}
