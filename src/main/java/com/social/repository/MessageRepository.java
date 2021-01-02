package com.social.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;

public interface MessageRepository {

  Message save(Message entity);

  Optional<Message> findByIdAndAuthor(Long id, User author);

  Page<Message> findAllByChat(Chat chat, Pageable pageable);

  void delete(Message entity);

}
