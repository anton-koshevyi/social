package com.social.backend.test.model.user;

import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

class JohnSmith extends User {

  JohnSmith() {
    super.setEmail("johnsmith@example.com");
    super.setUsername("johnsmith");
    super.setFirstName("John");
    super.setLastName("Smith");
    super.setPublicity(Publicity.PRIVATE);
    super.setPassword("{encoded}password");
    super.setModer(false);
    super.setAdmin(false);
  }

}
