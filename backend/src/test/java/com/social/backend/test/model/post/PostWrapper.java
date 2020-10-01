package com.social.backend.test.model.post;

import java.time.ZonedDateTime;

import com.social.backend.model.post.Post;

abstract class PostWrapper {

  private final Post model;

  PostWrapper(String title, String body) {
    model = new Post();
    model.setCreatedAt(ZonedDateTime.now());
    model.setTitle(title);
    model.setBody(body);
  }

  Post getModel() {
    return model;
  }

}
