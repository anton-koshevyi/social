package com.social.repository.spring;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;

public interface MessageRepositorySpring extends JpaRepository<Message, Long> {

  Optional<Message> findByIdAndAuthor(Long id, User author);

  Page<Message> findAllByChat(Chat chat, Pageable pageable);

}
