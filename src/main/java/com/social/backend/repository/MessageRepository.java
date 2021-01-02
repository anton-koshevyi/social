package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

public interface MessageRepository {

  Message save(Message entity);

  Optional<Message> findByIdAndAuthor(Long id, User author);

  Page<Message> findAllByChat(Chat chat, Pageable pageable);

  void delete(Message entity);

}
