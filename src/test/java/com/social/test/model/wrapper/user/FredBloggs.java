package com.social.test.model.wrapper.user;

import com.social.model.user.Publicity;

public class FredBloggs extends UserWrapper {

  public FredBloggs() {
    super(
        2L,
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
