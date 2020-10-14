package com.social.backend.test.model.type;

import com.social.backend.model.post.Post;

public enum PostType implements ModelType<Post> {

  COOKING,
  GAMING,
  READING;

  @Override
  public Class<Post> modelClass() {
    return Post.class;
  }

}
