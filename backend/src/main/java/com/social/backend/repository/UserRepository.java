package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
  
  Optional<User> findByEmail(String email);
  
  boolean existsByEmail(String email);
  
  Optional<User> findByUsername(String username);
  
  boolean existsByUsername(String username);
  
}
