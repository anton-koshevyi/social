package com.social.backend.repository.jpa;

import java.util.Optional;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.User;

public interface ChatJpaRepositoryGroup extends ChatJpaRepositoryBase<GroupChat> {

  Optional<GroupChat> findByIdAndOwner(Long id, User owner);

}
