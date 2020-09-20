package com.social.backend.repository.jpa;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

public interface MessageJpaRepository extends JpaRepository<Message, Long> {

  Optional<Message> findByIdAndAuthor(Long id, User author);

  Page<Message> findAllByChat(Chat chat, Pageable pageable);

}
