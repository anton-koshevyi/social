package com.social.repository.spring;

import java.util.List;

import com.social.model.chat.PrivateChat;
import com.social.model.user.User;

public interface SpringChatRepositoryPrivate extends SpringChatRepositoryBase<PrivateChat> {

  boolean existsByMembersIn(List<User> members);

}
