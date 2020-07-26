package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.chat.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByIdAndAuthorId(Long id, Long authorId);
    
    Page<Message> findAllByChatId(Long chatId, Pageable pageable);
}
