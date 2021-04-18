package com.social.repository.spring;

import java.util.Optional;

import com.social.model.chat.GroupChat;
import com.social.model.user.User;

public interface ChatRepositoryGroupSpring extends ChatRepositoryBaseSpring<GroupChat> {

  Optional<GroupChat> findByIdAndOwner(Long id, User owner);

}
