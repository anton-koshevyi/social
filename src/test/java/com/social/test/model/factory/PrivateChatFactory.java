package com.social.test.model.factory;

import com.social.model.chat.PrivateChat;
import com.social.test.model.type.ModelType;
import com.social.test.model.type.PrivateChatType;
import com.social.test.model.wrapper.AbstractWrapper;
import com.social.test.model.wrapper.ModelWrapper;
import com.social.test.model.wrapper.chat._private.Default;

class PrivateChatFactory extends AbstractFactory<PrivateChat> {

  @Override
  ModelWrapper<PrivateChat> createWrapper(ModelType<PrivateChat> type) {
    switch (Enum.valueOf(PrivateChatType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<PrivateChat>(new PrivateChat()) {};
      case DEFAULT:
        return new Default();
      default:
        return null;
    }
  }

}
