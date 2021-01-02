package com.social.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.user.User;

public interface UserRepository {

  User save(User entity);

  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Page<User> findAll(Pageable pageable);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  void delete(User entity);

}
