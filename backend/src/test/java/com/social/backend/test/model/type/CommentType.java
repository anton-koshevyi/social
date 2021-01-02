package com.social.backend.test.model.type;

import com.social.backend.model.post.Comment;

public enum CommentType implements ModelType<Comment> {

  RAW,
  LIKE,
  BADLY;

  @Override
  public Class<Comment> modelClass() {
    return Comment.class;
  }

}
