package com.social.test.model.factory;

import com.social.model.chat.Message;
import com.social.test.model.type.MessageType;
import com.social.test.model.type.ModelType;
import com.social.test.model.wrapper.AbstractWrapper;
import com.social.test.model.wrapper.ModelWrapper;
import com.social.test.model.wrapper.message.Meeting;
import com.social.test.model.wrapper.message.WhatsUp;

class MessageFactory extends AbstractFactory<Message> {

  @Override
  ModelWrapper<Message> createWrapper(ModelType<Message> type) {
    switch (Enum.valueOf(MessageType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<Message>(new Message()) {
        };
      case MEETING:
        return new Meeting();
      case WHATS_UP:
        return new WhatsUp();
      default:
        return null;
    }
  }

}
