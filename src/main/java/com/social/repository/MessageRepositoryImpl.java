package com.social.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;
import com.social.repository.jpa.MessageJpaRepository;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

  private final MessageJpaRepository delegate;

  @Autowired
  public MessageRepositoryImpl(MessageJpaRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public Message save(Message entity) {
    return delegate.save(entity);
  }

  @Override
  public Optional<Message> findByIdAndAuthor(Long id, User author) {
    return delegate.findByIdAndAuthor(id, author);
  }

  @Override
  public Page<Message> findAllByChat(Chat chat, Pageable pageable) {
    return delegate.findAllByChat(chat, pageable);
  }

  @Override
  public void delete(Message entity) {
    delegate.delete(entity);
  }

}
