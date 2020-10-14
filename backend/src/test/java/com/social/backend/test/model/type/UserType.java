package com.social.backend.test.model.type;

import com.social.backend.model.user.User;

public enum UserType implements ModelType<User> {

  FRED_BLOGGS,
  JANE_DOE,
  JOHN_SMITH;

  @Override
  public Class<User> modelClass() {
    return User.class;
  }

}
