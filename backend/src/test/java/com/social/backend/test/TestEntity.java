package com.social.backend.test;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.Message;

public final class TestEntity {

  private TestEntity() {
  }

  public static GroupChat groupChat() {
    GroupChat groupChat = new GroupChat();
    groupChat.setName("name");
    return groupChat;
  }

  public static Message message() {
    Message message = new Message();
    message.setBody("message body");
    return message;
  }

}
