package com.social.repository;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.PrivateChat;
import com.social.model.user.User;
import com.social.repository.spring.ChatRepositoryBaseSpring;
import com.social.repository.spring.ChatRepositoryGroupSpring;
import com.social.repository.spring.ChatRepositoryPrivateSpring;

@Repository
public class ChatRepositoryImpl implements ChatRepository {

  private final ChatRepositoryBaseSpring<Chat> baseDelegate;
  private final ChatRepositoryPrivateSpring privateDelegate;
  private final ChatRepositoryGroupSpring groupDelegate;

  @Autowired
  public ChatRepositoryImpl(ChatRepositoryBaseSpring<Chat> baseDelegate,
                            ChatRepositoryPrivateSpring privateDelegate,
                            ChatRepositoryGroupSpring groupDelegate) {
    this.baseDelegate = baseDelegate;
    this.privateDelegate = privateDelegate;
    this.groupDelegate = groupDelegate;
  }

  @Override
  public <T extends Chat> T save(T entity) {
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
