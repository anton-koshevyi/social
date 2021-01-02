package com.social.backend.test.model.wrapper.user;

import com.social.backend.model.user.User;
import com.social.backend.test.model.mutator.UserMutators;
import com.social.backend.test.model.wrapper.AbstractWrapper;

abstract class UserWrapper extends AbstractWrapper<User> {

  UserWrapper(Long id,
              String email,
              String username,
              String firstName,
              String lastName,
              Integer publicity,
              String password,
              boolean moder,
              boolean admin) {
    super(new User());
    super
        .with(UserMutators.id(id))
        .with(UserMutators.email(email))
        .with(UserMutators.username(username))
        .with(UserMutators.firstName(firstName))
        .with(UserMutators.lastName(lastName))
        .with(UserMutators.publicity(publicity))
        .with(UserMutators.password(password))
        .with(UserMutators.moder(moder))
        .with(UserMutators.admin(admin));
  }

}
