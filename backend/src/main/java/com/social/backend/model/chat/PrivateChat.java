package com.social.backend.model.chat;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ChatType.PRIVATE)
public class PrivateChat extends Chat {
}
