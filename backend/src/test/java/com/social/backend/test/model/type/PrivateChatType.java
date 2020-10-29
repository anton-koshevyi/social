package com.social.backend.test.model.type;

import com.social.backend.model.chat.PrivateChat;

public enum PrivateChatType implements ModelType<PrivateChat> {

  RAW,
  DEFAULT;

  @Override
  public Class<PrivateChat> modelClass() {
    return PrivateChat.class;
  }

}
