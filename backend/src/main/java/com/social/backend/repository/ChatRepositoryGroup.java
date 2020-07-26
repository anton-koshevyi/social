package com.social.backend.repository;

import java.util.Optional;

import com.social.backend.model.chat.GroupChat;

public interface ChatRepositoryGroup extends ChatRepositoryBase<GroupChat> {
    Optional<GroupChat> findByIdAndOwnerId(Long id, Long ownerId);
}
