package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.conversation.Conversation;
import com.social.backend.model.user.User;

public interface ConversationBaseRepository<T extends Conversation> extends JpaRepository<T, Long> {
    Optional<T> findByIdAndMembersContaining(Long id, User user);
    
    Page<T> findAllByMembersContaining(User user, Pageable pageable);
}
