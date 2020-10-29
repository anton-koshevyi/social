package com.social.backend.test.model.factory;

import com.social.backend.model.post.Post;
import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.type.PostType;
import com.social.backend.test.model.wrapper.AbstractWrapper;
import com.social.backend.test.model.wrapper.ModelWrapper;
import com.social.backend.test.model.wrapper.post.Cooking;
import com.social.backend.test.model.wrapper.post.Gaming;
import com.social.backend.test.model.wrapper.post.Reading;

class PostFactory extends AbstractFactory<Post> {

  @Override
  ModelWrapper<Post> createWrapper(ModelType<Post> type) {
    switch (Enum.valueOf(PostType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<Post>(new Post()) {};
      case COOKING:
        return new Cooking();
      case GAMING:
        return new Gaming();
      case READING:
        return new Reading();
      default:
        return null;
    }
  }

}
