package com.social.backend.test.model.user;

import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

class JaneDoe extends User {

  JaneDoe() {
    super.setEmail("janedoe@example.com");
    super.setUsername("janedoe");
    super.setFirstName("Jane");
    super.setLastName("Doe");
    super.setPublicity(Publicity.PRIVATE);
    super.setPassword("{encoded}password");
    super.setModer(false);
    super.setAdmin(false);
  }

}
