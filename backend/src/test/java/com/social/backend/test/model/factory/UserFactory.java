package com.social.backend.test.model.factory;

import com.social.backend.model.user.User;
import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.type.UserType;
import com.social.backend.test.model.wrapper.AbstractWrapper;
import com.social.backend.test.model.wrapper.ModelWrapper;
import com.social.backend.test.model.wrapper.user.FredBloggs;
import com.social.backend.test.model.wrapper.user.JohnSmith;

class UserFactory extends AbstractFactory<User> {

  @Override
  ModelWrapper<User> createWrapper(ModelType<User> type) {
    switch (Enum.valueOf(UserType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<User>(new User()) {};
      case JOHN_SMITH:
        return new JohnSmith();
      case FRED_BLOGGS:
        return new FredBloggs();
      default:
        return null;
    }
  }

}
