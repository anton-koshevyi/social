package com.social.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.PrivateChat;
import com.social.model.user.User;

public interface ChatRepository {

  <T extends Chat> T save(T entity);

  Optional<Chat> findByIdAndMember(Long id, User member);

  Optional<PrivateChat> findPrivateByIdAndMember(Long id, User member);

  Optional<GroupChat> findGroupByIdAndMember(Long id, User member);

  Optional<GroupChat> findGroupByIdAndOwner(Long id, User owner);

  boolean existsPrivateByMembers(User... members);

  Page<Chat> findAllByMember(User member, Pageable pageable);

  void delete(Chat entity);

}
