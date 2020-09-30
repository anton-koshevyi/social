package com.social.backend.test.model.user;

import com.social.backend.model.user.Publicity;

class JohnSmith extends UserWrapper {

  JohnSmith() {
    super(
        "johnsmith@example.com",
        "johnsmith",
        "John",
        "Smith",
        Publicity.PRIVATE,
        "{encoded}password",
        false,
        false
    );
  }

}
