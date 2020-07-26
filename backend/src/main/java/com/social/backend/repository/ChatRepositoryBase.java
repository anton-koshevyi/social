package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;

public interface ChatRepositoryBase<T extends Chat> extends JpaRepository<T, Long> {
    Optional<T> findByIdAndMembersContaining(Long id, User user);
    
    Page<T> findAllByMembersContaining(User user, Pageable pageable);
}
