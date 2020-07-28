package com.social.backend.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;

public interface ChatService {
    Chat createPrivate(User user, User target);
    
    void deletePrivate(Long id, User user);
    
    Chat createGroup(User creator, String name, Set<User> members);
    
    Chat updateGroup(Long id, User member, String name);
    
    Chat updateGroupMembers(Long id, Long ownerId, Set<User> members);
    
    Chat setOwner(Long id, Long ownerId, User newOwner);
    
    void leaveGroup(Long id, User member);
    
    void deleteGroup(Long id, Long ownerId);
    
    Page<User> getMembers(Long id, User member, Pageable pageable);
    
    Chat findByIdAndUser(Long id, User user);
    
    Page<Chat> findAllByUser(User user, Pageable pageable);
}
