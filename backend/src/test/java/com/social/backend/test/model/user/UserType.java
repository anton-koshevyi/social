package com.social.backend.test.model.user;

import com.social.backend.model.user.User;
import com.social.backend.test.model.ModelType;

public enum UserType implements ModelType<User> {

  FRED_BLOGGS,
  JANE_DOE,
  JOHN_SMITH;

  @Override
  public Class<User> modelClass() {
    return User.class;
  }

}
