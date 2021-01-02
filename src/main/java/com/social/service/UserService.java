package com.social.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.user.User;

public interface UserService {
  
  User create(String email,
              String username,
              String firstName,
              String lastName,
              String password);
  
  User update(Long id,
              String email,
              String username,
              String firstName,
              String lastName,
              Integer publicity);
  
  User updateRole(Long id, Boolean moder);
  
  void changePassword(Long id, String actual, String change);
  
  void delete(Long id, String password);
  
  void addFriend(Long id, Long targetId);
  
  void removeFriend(Long id, Long targetId);
  
  Page<User> getFriends(Long id, Pageable pageable);
  
  User find(Long id);
  
  Page<User> findAll(Pageable pageable);
  
}
