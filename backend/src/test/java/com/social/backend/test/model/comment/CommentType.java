package com.social.backend.test.model.comment;

import com.social.backend.model.post.Comment;
import com.social.backend.test.model.ModelType;

public enum CommentType implements ModelType<Comment> {

  BADLY,
  LIKE,
  USEFUL;

  @Override
  public Class<Comment> modelClass() {
    return Comment.class;
  }

}
