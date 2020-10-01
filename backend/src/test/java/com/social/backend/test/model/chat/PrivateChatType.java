package com.social.backend.test.model.chat;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.test.model.ModelType;

public enum PrivateChatType implements ModelType<PrivateChat> {

  RAW;

  @Override
  public Class<PrivateChat> modelClass() {
    return PrivateChat.class;
  }

}
