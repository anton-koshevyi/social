package com.social.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;

public interface ChatService {
    Chat createPrivate(User user, User target);
    
    Chat createGroup(User user, String name, List<User> members);
    
    Chat updateGroup(Long id, User user, String name, List<User> newMembers);
    
    Chat setOwner(Long id, Long ownerId, User newOwner);
    
    void leaveGroup(Long id, User user);
    
    void removeGroupMembers(Long id, Long ownerId, List<User> members);
    
    void deletePrivate(Long id, User user);
    
    void deleteGroup(Long id, Long ownerId);
    
    Chat findByIdAndUser(Long id, User user);
    
    Page<Chat> findAllByUser(User user, Pageable pageable);
}
