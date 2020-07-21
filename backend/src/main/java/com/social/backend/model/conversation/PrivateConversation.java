package com.social.backend.model.conversation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ConversationType.PRIVATE)
public class PrivateConversation extends Conversation {
}
