package com.social.backend.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;

public interface ChatService {
  
  Chat createPrivate(User user, User target);
  
  void deletePrivate(Long id, User member);
  
  Chat createGroup(User creator, String name, Set<User> members);
  
  Chat updateGroup(Long id, User member, String name);
  
  Chat updateGroupMembers(Long id, User owner, Set<User> members);
  
  Chat changeOwner(Long id, User owner, User newOwner);
  
  void leaveGroup(Long id, User member);
  
  void deleteGroup(Long id, User owner);
  
  Page<User> getMembers(Long id, User member, Pageable pageable);
  
  Chat find(Long id, User member);
  
  Page<Chat> findAll(User member, Pageable pageable);
  
}
