package com.social.backend.test.model.post;

import com.social.backend.model.post.Post;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.ModelType;

public class PostFactory extends ModelFactory<Post> {

  @Override
  public Post createModel(ModelType<Post> type) {
    switch (Enum.valueOf(PostType.class, type.name())) {
      case COOKING:
        return new Cooking().getModel();
      case GAMING:
        return new Gaming().getModel();
      case READING:
        return new Reading().getModel();
      default:
        return null;
    }
  }

}
