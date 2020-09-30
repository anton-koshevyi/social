package com.social.backend.test.model.post;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.ModelType;

public class PostFactory extends ModelFactory<Post> {

  @Override
  public Post createModel(ModelType<Post> type) {
    User author = new User();

    switch (Enum.valueOf(PostType.class, type.name())) {
      case COOKING:
        return new Cooking(author).getModel();
      case GAMING:
        return new Gaming(author).getModel();
      case READING:
        return new Reading(author).getModel();
      default:
        return null;
    }
  }

}
