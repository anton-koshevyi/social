package com.social.repository.spring;

import java.util.Optional;

import com.social.model.chat.GroupChat;
import com.social.model.user.User;

public interface SpringChatRepositoryGroup extends SpringChatRepositoryBase<GroupChat> {

  Optional<GroupChat> findByIdAndOwner(Long id, User owner);

}
