package com.social.repository.jpa;

import java.util.Optional;

import com.social.model.chat.GroupChat;
import com.social.model.user.User;

public interface ChatJpaRepositoryGroup extends ChatJpaRepositoryBase<GroupChat> {

  Optional<GroupChat> findByIdAndOwner(Long id, User owner);

}
