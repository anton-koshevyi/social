package com.social.test.model.type;

import com.social.model.chat.Message;

public enum MessageType implements ModelType<Message> {

  RAW,
  WHATS_UP,
  MEETING;

  @Override
  public Class<Message> modelClass() {
    return Message.class;
  }

}
