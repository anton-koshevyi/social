package com.social.backend.test.model.wrapper.user;

import com.social.backend.model.user.Publicity;

public class JaneDoe extends UserWrapper {

  public JaneDoe() {
    super(
        3L,
        "janedoe@example.com",
        "janedoe",
        "Jane",
        "Doe",
        Publicity.PRIVATE,
        "{encoded}password",
        false,
        false
    );
  }

}
