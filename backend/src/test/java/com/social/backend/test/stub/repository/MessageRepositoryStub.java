package com.social.backend.test.stub.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.repository.MessageRepository;
import com.social.backend.test.stub.repository.identification.Identification;

public class MessageRepositoryStub
    extends IdentifyingRepositoryStub<Long, Message>
    implements MessageRepository {

  public MessageRepositoryStub(Identification<Message> identification) {
    super(identification);
  }

  @Override
  protected Long getId(Message entity) {
    return entity.getId();
  }

  @Override
  public Message save(Message entity) {
    return super.save(entity);
  }

  @Override
  public Optional<Message> findByIdAndAuthor(Long id, User author) {
    Message entity = super.find(message ->
        Objects.equals(message.getId(), id)
            && Objects.equals(message.getAuthor(), author));
    return Optional.ofNullable(entity);
  }

  @Override
  public Page<Message> findAllByChat(Chat chat, Pageable pageable) {
    List<Message> entities = super.findAll().stream()
        .filter(message -> Objects.equals(chat, message.getChat()))
        .collect(Collectors.toList());
    return new PageImpl<>(entities);
  }

  @Override
  public void delete(Message entity) {
    super.delete(entity);
  }

}
