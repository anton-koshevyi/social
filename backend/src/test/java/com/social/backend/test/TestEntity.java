package com.social.backend.test;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.post.Comment;

public final class TestEntity {

  private TestEntity() {
  }

  public static Comment comment() {
    Comment comment = new Comment();
    comment.setBody("comment body");
    return comment;
  }

  public static PrivateChat privateChat() {
    return new PrivateChat();
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
