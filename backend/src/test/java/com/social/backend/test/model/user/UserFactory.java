package com.social.backend.test.model.user;

import com.social.backend.model.user.User;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.ModelType;

public class UserFactory extends ModelFactory<User> {

  @Override
  public User createModel(ModelType<User> type) {
    switch (Enum.valueOf(UserType.class, type.name())) {
      case JOHN_SMITH:
        return new JohnSmith().getModel();
      case JANE_DOE:
        return new JaneDoe().getModel();
      case FRED_BLOGGS:
        return new FredBloggs().getModel();
      default:
        return null;
    }
  }

}
