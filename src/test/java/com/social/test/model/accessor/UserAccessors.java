package com.social.test.model.accessor;

import com.social.model.user.User;

public final class UserAccessors {

  private UserAccessors() {
  }

  public static Long id(User m) {
    return m.getId();
  }


}
