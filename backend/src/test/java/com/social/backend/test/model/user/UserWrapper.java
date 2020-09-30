package com.social.backend.test.model.user;

import com.social.backend.model.user.User;

abstract class UserWrapper {

  private final User model;

  UserWrapper(String email,
              String username,
              String firstName,
              String lastName,
              Integer publicity,
              String password,
              boolean moder,
              boolean admin) {
    model = new User();
    model.setEmail(email);
    model.setUsername(username);
    model.setFirstName(firstName);
    model.setLastName(lastName);
    model.setPublicity(publicity);
    model.setPassword(password);
    model.setModer(moder);
    model.setAdmin(admin);
  }

  User getModel() {
    return model;
  }

}
