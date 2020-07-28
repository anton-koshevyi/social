package com.social.backend.repository;

import java.util.Optional;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.User;

public interface ChatRepositoryGroup extends ChatRepositoryBase<GroupChat> {
    Optional<GroupChat> findByIdAndOwner(Long id, User owner);
}
