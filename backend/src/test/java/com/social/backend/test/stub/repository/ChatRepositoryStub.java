package com.social.backend.test.stub.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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
    Chat entity = super.find(
        byId(id)
            .and(byMember(member))
    );
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<PrivateChat> findPrivateByIdAndMember(Long id, User member) {
    PrivateChat entity = (PrivateChat) super.find(
        byType(PrivateChat.class)
            .and(byId(id))
            .and(byMember(member))
    );
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<GroupChat> findGroupByIdAndMember(Long id, User member) {
    GroupChat entity = (GroupChat) super.find(
        byType(GroupChat.class)
            .and(byId(id))
            .and(byMember(member))
    );
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<GroupChat> findGroupByIdAndOwner(Long id, User owner) {
    GroupChat entity = (GroupChat) super.find(
        byType(GroupChat.class)
            .and(byId(id))
            .and(byOwner(owner))
    );
    return Optional.ofNullable(entity);
  }

  @Override
  public boolean existsPrivateByMembers(User... members) {
    return super.exists(
        byType(PrivateChat.class)
            .and(byMembers(members))
    );
  }

  @Override
  public Page<Chat> findAllByMember(User member, Pageable pageable) {
    List<Chat> entities = super.findAll().stream()
        .filter(byMember(member))
        .collect(Collectors.toList());
    return new PageImpl<>(entities);
  }

  @Override
  public void delete(Chat entity) {
    super.delete(entity);
  }

  private static Predicate<Chat> byType(Class<? extends Chat> type) {
    return e -> type.isAssignableFrom(e.getClass());
  }

  private static Predicate<Chat> byId(Long id) {
    return e -> id.equals(e.getId());
  }

  private static Predicate<Chat> byMember(User member) {
    return e -> e.getMembers().stream()
        .map(User::getId)
        .anyMatch(member.getId()::equals);
  }

  private static Predicate<Chat> byOwner(User owner) {
    return e -> owner.getId().equals(((GroupChat) e).getOwner().getId());
  }

  private static Predicate<Chat> byMembers(User... members) {
    return e -> e.getMembers().stream()
        .map(User::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.stream(members)
            .map(User::getId)
            .collect(Collectors.toList()));
  }

}
