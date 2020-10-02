package com.social.backend.test.stub.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepository;
import com.social.backend.test.stub.repository.identification.Identification;

public class ChatRepositoryStub
    extends IdentifyingRepositoryStub<Long, Chat>
    implements ChatRepository {

  public ChatRepositoryStub(Identification<Chat> identification) {
    super(identification);
  }

  @Override
  protected Long getId(Chat entity) {
    return entity.getId();
  }

  @Override
  public Chat save(Chat entity) {
    return super.save(entity);
  }

  @Override
  public Optional<Chat> findByIdAndMember(Long id, User member) {
    Chat entity = super.find(chat ->
        Objects.equals(chat.getId(), id)
            && chat.getMembers().contains(member));
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<PrivateChat> findPrivateByIdAndMember(Long id, User member) {
    PrivateChat entity = (PrivateChat) super.find(chat ->
        (chat instanceof PrivateChat)
            && Objects.equals(chat.getId(), id)
            && chat.getMembers().contains(member));
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<GroupChat> findGroupByIdAndMember(Long id, User member) {
    GroupChat entity = (GroupChat) super.find(chat ->
        (chat instanceof GroupChat)
            && Objects.equals(chat.getId(), id)
            && chat.getMembers().contains(member));
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<GroupChat> findGroupByIdAndOwner(Long id, User owner) {
    GroupChat entity = (GroupChat) super.find(chat ->
        (chat instanceof GroupChat)
            && Objects.equals(chat.getId(), id)
            && Objects.equals(((GroupChat) chat).getOwner(), owner));
    return Optional.ofNullable(entity);
  }

  @Override
  public boolean existsPrivateByMembers(User... members) {
    return super.exists(chat ->
        (chat instanceof PrivateChat)
            && chat.getMembers().containsAll(Arrays.asList(members)));
  }

  @Override
  public Page<Chat> findAllByMember(User member, Pageable pageable) {
    List<Chat> entities = super.findAll().stream()
        .filter(chat -> chat.getMembers().contains(member))
        .collect(Collectors.toList());
    return new PageImpl<>(entities);
  }

  @Override
  public void delete(Chat entity) {
    super.delete(entity);
  }

}
