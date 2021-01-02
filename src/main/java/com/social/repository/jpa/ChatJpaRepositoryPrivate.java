package com.social.repository.jpa;

import java.util.List;

import com.social.model.chat.PrivateChat;
import com.social.model.user.User;

public interface ChatJpaRepositoryPrivate extends ChatJpaRepositoryBase<PrivateChat> {

  boolean existsByMembersIn(List<User> members);

}
