package com.social.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.conversation.Conversation;
import com.social.backend.model.user.User;

public interface ConversationService {
    Conversation createPrivate(User user, User target);
    
    Conversation createGroup(User user, String name, List<User> members);
    
    Conversation updateGroup(Long id, User user, String name, List<User> newMembers);
    
    Conversation setOwner(Long id, Long ownerId, User newOwner);
    
    void leaveGroup(Long id, User user);
    
    void removeGroupMembers(Long id, Long ownerId, List<User> members);
    
    void deletePrivate(Long id, User user);
    
    void deleteGroup(Long id, Long ownerId);
    
    Conversation findByIdAndUser(Long id, User user);
    
    Page<Conversation> findAllByUser(User user, Pageable pageable);
}
