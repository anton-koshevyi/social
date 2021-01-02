package com.social.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.PrivateChat;
import com.social.model.user.User;

public interface ChatService {

  PrivateChat createPrivate(User user, User target);

  void deletePrivate(Long id, User member);

  GroupChat createGroup(User creator, String name, Set<User> members);

  GroupChat updateGroup(Long id, User member, String name);

  GroupChat updateGroupMembers(Long id, User owner, Set<User> members);

  GroupChat changeOwner(Long id, User owner, User newOwner);

  void leaveGroup(Long id, User member);

  void deleteGroup(Long id, User owner);

  Page<User> getMembers(Long id, User member, Pageable pageable);

  Chat find(Long id, User member);

  Page<Chat> findAll(User member, Pageable pageable);

}
