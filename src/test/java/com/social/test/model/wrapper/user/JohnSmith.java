package com.social.test.model.wrapper.user;

import com.social.model.user.Publicity;

public class JohnSmith extends UserWrapper {

  public JohnSmith() {
    super(
        1L,
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
