package com.social.repository.spring;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social.model.user.User;

public interface UserRepositorySpring extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

}
