package com.social.backend.test.model.user;

import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

class FredBloggs extends User {

  FredBloggs() {
    super.setEmail("fredbloggs@example.com");
    super.setUsername("fredbloggs");
    super.setFirstName("Fred");
    super.setLastName("Bloggs");
    super.setPublicity(Publicity.PRIVATE);
    super.setPassword("{encoded}password");
    super.setModer(false);
    super.setAdmin(false);
  }

}
