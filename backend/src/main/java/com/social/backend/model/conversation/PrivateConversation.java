package com.social.backend.model.conversation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("private")
public class PrivateConversation extends Conversation {
}
