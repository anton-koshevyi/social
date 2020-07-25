package com.social.backend.repository;

import java.util.Optional;

import com.social.backend.model.conversation.GroupConversation;

public interface ConversationGroupRepository extends ConversationBaseRepository<GroupConversation> {
    Optional<GroupConversation> findByIdAndOwnerId(Long id, Long ownerId);
}
