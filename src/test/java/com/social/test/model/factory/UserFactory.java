package com.social.test.model.factory;

import com.social.model.user.User;
import com.social.test.model.type.ModelType;
import com.social.test.model.type.UserType;
import com.social.test.model.wrapper.AbstractWrapper;
import com.social.test.model.wrapper.ModelWrapper;
import com.social.test.model.wrapper.user.FredBloggs;
import com.social.test.model.wrapper.user.JohnSmith;

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
