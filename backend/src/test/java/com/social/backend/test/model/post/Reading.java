package com.social.backend.test.model.post;

import com.social.backend.model.user.User;

class Reading extends PostWrapper {

  Reading(User author) {
    super(
        "Favorite books",
        "My personal must-read fiction",
        author
    );
  }

}
