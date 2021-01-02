package com.social.test.model.type;

import com.social.model.chat.PrivateChat;

public enum PrivateChatType implements ModelType<PrivateChat> {

  RAW,
  DEFAULT;

  @Override
  public Class<PrivateChat> modelClass() {
    return PrivateChat.class;
  }

}
