package com.social.backend.test.model.post;

import java.time.ZonedDateTime;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

abstract class PostWrapper {

  private final Post model;

  PostWrapper(String title, String body, User author) {
    model = new Post();
    model.setCreatedAt(ZonedDateTime.now());
    model.setTitle(title);
    model.setBody(body);
    model.setAuthor(author);
  }

  public Post getModel() {
    return model;
  }

}
