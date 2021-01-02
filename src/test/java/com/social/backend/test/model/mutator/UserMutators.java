package com.social.backend.test.model.mutator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.social.backend.model.user.User;

public final class UserMutators {

  private UserMutators() {
  }

  public static Consumer<User> id(Long v) {
    return m -> m.setId(v);
  }

  public static Consumer<User> email(String v) {
    return m -> m.setEmail(v);
  }

  public static Consumer<User> username(String v) {
    return m -> m.setUsername(v);
  }

  public static Consumer<User> firstName(String v) {
    return m -> m.setFirstName(v);
  }

  public static Consumer<User> lastName(String v) {
    return m -> m.setLastName(v);
  }

  public static Consumer<User> publicity(int v) {
    return m -> m.setPublicity(v);
  }

  public static Consumer<User> password(String v) {
    return m -> m.setPassword(v);
  }

  public static Consumer<User> moder(boolean v) {
    return m -> m.setModer(v);
  }

  public static Consumer<User> admin(boolean v) {
    return m -> m.setAdmin(v);
  }

  public static Consumer<User> friends(User... v) {
    Set<User> friends = new HashSet<>();
    Collections.addAll(friends, v);
    return m -> m.setFriends(friends);
  }

}
