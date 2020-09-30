package com.social.backend.test.model.user;

import com.social.backend.model.user.Publicity;

class JaneDoe extends UserWrapper {

  JaneDoe() {
    super(
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
