package com.social.backend.test.model.type;

import com.social.backend.model.chat.Message;

public enum MessageType implements ModelType<Message> {

  MEETING,
  OK,
  WHATS_UP;

  @Override
  public Class<Message> modelClass() {
    return Message.class;
  }

}
