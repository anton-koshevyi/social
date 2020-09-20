package com.social.backend.repository.jpa;

import java.util.List;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

public interface ChatJpaRepositoryPrivate extends ChatJpaRepositoryBase<PrivateChat> {

  boolean existsByMembersIn(List<User> members);

}
