package com.social.backend.test.model.message;

import com.social.backend.model.chat.Message;
import com.social.backend.test.model.ModelType;

public enum MessageType implements ModelType<Message> {

  MEETING,
  OK,
  WHATS_UP;

  @Override
  public Class<Message> modelClass() {
    return Message.class;
  }

}
