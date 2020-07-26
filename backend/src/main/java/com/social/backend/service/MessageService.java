package com.social.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

public interface MessageService {
    Message create(Chat chat, User author, String body);
    
    Message update(Long id, Long authorId, String body);
    
    void delete(Long id, Long authorId);
    
    Page<Message> findAllByChatId(Long chatId, Pageable pageable);
}
