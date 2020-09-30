package com.social.backend.test.model.post;

import com.social.backend.model.user.User;

class Gaming extends PostWrapper {

  Gaming(User author) {
    super(
        "Best games ever",
        "Most popular games 2000-2020",
        author
    );
  }

}
