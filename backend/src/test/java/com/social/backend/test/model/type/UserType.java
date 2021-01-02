package com.social.backend.test.model.type;

import com.social.backend.model.user.User;

public enum UserType implements ModelType<User> {

  RAW,
  JOHN_SMITH,
  FRED_BLOGGS;

  @Override
  public Class<User> modelClass() {
    return User.class;
  }

}
