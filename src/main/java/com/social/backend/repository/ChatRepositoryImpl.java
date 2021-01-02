package com.social.backend.repository;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;
import com.social.backend.repository.jpa.ChatJpaRepositoryBase;
import com.social.backend.repository.jpa.ChatJpaRepositoryGroup;
import com.social.backend.repository.jpa.ChatJpaRepositoryPrivate;

@Repository
public class ChatRepositoryImpl implements ChatRepository {

  private final ChatJpaRepositoryBase<Chat> baseDelegate;
  private final ChatJpaRepositoryPrivate privateDelegate;
  private final ChatJpaRepositoryGroup groupDelegate;

  @Autowired
  public ChatRepositoryImpl(ChatJpaRepositoryBase<Chat> baseDelegate,
                            ChatJpaRepositoryPrivate privateDelegate,
                            ChatJpaRepositoryGroup groupDelegate) {
    this.baseDelegate = baseDelegate;
    this.privateDelegate = privateDelegate;
    this.groupDelegate = groupDelegate;
  }

  @Override
  public Chat save(Chat entity) {
    return baseDelegate.save(entity);
  }

  @Override
  public Optional<Chat> findByIdAndMember(Long id, User member) {
    return baseDelegate.findByIdAndMembersContaining(id, member);
  }

  @Override
  public Optional<PrivateChat> findPrivateByIdAndMember(Long id, User member) {
    return privateDelegate.findByIdAndMembersContaining(id, member);
  }

  @Override
  public Optional<GroupChat> findGroupByIdAndMember(Long id, User member) {
    return groupDelegate.findByIdAndMembersContaining(id, member);
  }

  @Override
  public Optional<GroupChat> findGroupByIdAndOwner(Long id, User owner) {
    return groupDelegate.findByIdAndOwner(id, owner);
  }

  @Override
  public boolean existsPrivateByMembers(User... members) {
    return privateDelegate.existsByMembersIn(Arrays.asList(members));
  }

  @Override
  public Page<Chat> findAllByMember(User member, Pageable pageable) {
    return baseDelegate.findAllByMembersContaining(member, pageable);
  }

  @Override
  public void delete(Chat entity) {
    baseDelegate.delete(entity);
  }

}
