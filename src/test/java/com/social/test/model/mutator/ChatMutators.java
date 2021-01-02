package com.social.test.model.mutator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.user.User;

public final class ChatMutators {

  private ChatMutators() {
  }

  public static <T extends Chat> Consumer<T> id(Long v) {
    return m -> m.setId(v);
  }

  public static <T extends Chat> Consumer<T> members(User... v) {
    Set<User> members = new HashSet<>();
    Collections.addAll(members, v);
    return m -> m.setMembers(members);
  }

  public static Consumer<GroupChat> name(String v) {
    return m -> m.setName(v);
  }

  public static Consumer<GroupChat> owner(User v) {
    return m -> m.setOwner(v);
  }

}
