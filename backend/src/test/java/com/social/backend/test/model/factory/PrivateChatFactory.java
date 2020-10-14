package com.social.backend.test.model.factory;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.type.PrivateChatType;
import com.social.backend.test.model.wrapper.AbstractWrapper;
import com.social.backend.test.model.wrapper.ModelWrapper;

class PrivateChatFactory extends AbstractFactory<PrivateChat> {

  @Override
  ModelWrapper<PrivateChat> createWrapper(ModelType<PrivateChat> type) {
    switch (Enum.valueOf(PrivateChatType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<PrivateChat>(new PrivateChat()) {};
      default:
        return null;
    }
  }

}
