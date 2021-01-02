package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

public interface ChatRepository {

  Chat save(Chat entity);

  Optional<Chat> findByIdAndMember(Long id, User member);

  Optional<PrivateChat> findPrivateByIdAndMember(Long id, User member);

  Optional<GroupChat> findGroupByIdAndMember(Long id, User member);

  Optional<GroupChat> findGroupByIdAndOwner(Long id, User owner);

  boolean existsPrivateByMembers(User... members);

  Page<Chat> findAllByMember(User member, Pageable pageable);

  void delete(Chat entity);

}
