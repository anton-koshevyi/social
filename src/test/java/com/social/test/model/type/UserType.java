package com.social.test.model.type;

import com.social.model.user.User;

public enum UserType implements ModelType<User> {

  RAW,
  JOHN_SMITH,
  FRED_BLOGGS;

  @Override
  public Class<User> modelClass() {
    return User.class;
  }

}
