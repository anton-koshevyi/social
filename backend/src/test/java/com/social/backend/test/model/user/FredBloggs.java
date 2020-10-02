package com.social.backend.test.model.user;

import com.social.backend.model.user.Publicity;

class FredBloggs extends UserWrapper {

  FredBloggs() {
    super(
        "fredbloggs@example.com",
        "fredbloggs",
        "Fred",
        "Bloggs",
        Publicity.PRIVATE,
        "{encoded}password",
        false,
        false
    );
  }

}
