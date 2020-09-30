package com.social.backend.test.model.post;

import com.social.backend.model.user.User;

class Cooking extends PostWrapper {

  Cooking(User author) {
    super(
        "Cooking of omelet",
        "How to cook an omelet",
        author
    );
  }

}
