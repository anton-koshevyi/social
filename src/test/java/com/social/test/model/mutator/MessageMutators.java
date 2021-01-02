package com.social.test.model.mutator;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;

public final class MessageMutators {

  private MessageMutators() {
  }

  public static Consumer<Message> id(Long v) {
    return m -> m.setId(v);
  }

  public static Consumer<Message> createdAt(ZonedDateTime v) {
    return m -> m.setCreatedAt(v);
  }

  public static Consumer<Message> updatedAt(ZonedDateTime v) {
    return m -> m.setUpdatedAt(v);
  }

  public static Consumer<Message> body(String v) {
    return m -> m.setBody(v);
  }

  public static Consumer<Message> author(User v) {
    return m -> m.setAuthor(v);
  }

  public static Consumer<Message> chat(Chat v) {
    return m -> m.setChat(v);
  }

}
