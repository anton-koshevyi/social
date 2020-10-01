package com.social.backend.test.model.chat;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.ModelType;

public class PrivateChatFactory extends ModelFactory<PrivateChat> {

  @Override
  public PrivateChat createModel(ModelType<PrivateChat> type) {
    switch (Enum.valueOf(PrivateChatType.class, type.name())) {
      case RAW:
        return new PrivateChat();
      default:
        return null;
    }
  }

}
