package com.social.backend.test.stub.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;
import com.social.backend.test.stub.repository.identification.Identification;

public class UserRepositoryStub
    extends IdentifyingRepositoryStub<Long, User>
    implements UserRepository {

  public UserRepositoryStub(Identification<User> identification) {
    super(identification);
  }

  @Override
  protected Long getId(User entity) {
    return entity.getId();
  }

  @Override
  public User save(User user) {
    return super.save(user);
  }

  @Override
  public Optional<User> findById(Long id) {
    User entity = super.find(id);
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    User entity = super.find(byEmail(email));
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    User entity = super.find(byUsername(username));
    return Optional.ofNullable(entity);
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    List<User> entities = super.findAll();
    return new PageImpl<>(entities);
  }

  @Override
  public boolean existsByEmail(String email) {
    return super.exists(byEmail(email));
  }

  @Override
  public boolean existsByUsername(String username) {
    return super.exists(byUsername(username));
  }

  @Override
  public void delete(User entity) {
    super.delete(entity);
  }

  private static Predicate<User> byEmail(String email) {
    return e -> email.equals(e.getEmail());
  }

  private static Predicate<User> byUsername(String username) {
    return e -> username.equals(e.getUsername());
  }

}
