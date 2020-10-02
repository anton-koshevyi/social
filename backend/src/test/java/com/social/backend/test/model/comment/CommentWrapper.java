package com.social.backend.test.model.comment;

import com.social.backend.model.post.Comment;

abstract class CommentWrapper {

  private final Comment model;

  CommentWrapper(String body) {
    model = new Comment();
    model.setBody(body);
  }

  Comment getModel() {
    return model;
  }

}
