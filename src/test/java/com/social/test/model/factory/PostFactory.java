package com.social.test.model.factory;

import com.social.model.post.Post;
import com.social.test.model.type.ModelType;
import com.social.test.model.type.PostType;
import com.social.test.model.wrapper.AbstractWrapper;
import com.social.test.model.wrapper.ModelWrapper;
import com.social.test.model.wrapper.post.Cooking;
import com.social.test.model.wrapper.post.Reading;

class PostFactory extends AbstractFactory<Post> {

  @Override
  ModelWrapper<Post> createWrapper(ModelType<Post> type) {
    switch (Enum.valueOf(PostType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<Post>(new Post()) {};
      case COOKING:
        return new Cooking();
      case READING:
        return new Reading();
      default:
        return null;
    }
  }

}
