package com.social.backend.test.model.message;

import com.social.backend.model.chat.Message;

abstract class MessageWrapper {

  private final Message model;

  MessageWrapper(String body) {
    model = new Message();
    model.setBody(body);
  }

  Message getModel() {
    return model;
  }

}
