package com.social.backend.test.model.message;

import com.social.backend.model.chat.Message;
import com.social.backend.test.model.AbstractFactory;
import com.social.backend.test.model.ModelType;

public class MessageFactory extends AbstractFactory<Message> {

  @Override
  public Message createModel(ModelType<Message> type) {
    switch (Enum.valueOf(MessageType.class, type.name())) {
      case MEETING:
        return new Meeting().getModel();
      case OK:
        return new Ok().getModel();
      case WHATS_UP:
        return new WhatsUp().getModel();
      default:
        return null;
    }
  }

}
