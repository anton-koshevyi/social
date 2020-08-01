package com.social.backend.repository;

import java.util.List;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

public interface ChatRepositoryPrivate extends ChatRepositoryBase<PrivateChat> {
  
  boolean existsByMembersIn(List<User> members);
  
}
