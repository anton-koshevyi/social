package com.social.backend.test.model.chat;

import com.social.backend.model.chat.GroupChat;

abstract class GroupChatWrapper {

  private final GroupChat model;

  GroupChatWrapper(String name) {
    model = new GroupChat();
    model.setName(name);
  }

  GroupChat getModel() {
    return model;
  }

}
