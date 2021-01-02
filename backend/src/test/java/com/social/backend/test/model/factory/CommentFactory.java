package com.social.backend.test.model.factory;

import com.social.backend.model.post.Comment;
import com.social.backend.test.model.type.CommentType;
import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.wrapper.AbstractWrapper;
import com.social.backend.test.model.wrapper.ModelWrapper;
import com.social.backend.test.model.wrapper.comment.Badly;
import com.social.backend.test.model.wrapper.comment.Like;

class CommentFactory extends AbstractFactory<Comment> {

  @Override
  ModelWrapper<Comment> createWrapper(ModelType<Comment> type) {
    switch (Enum.valueOf(CommentType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<Comment>(new Comment()) {};
      case BADLY:
        return new Badly();
      case LIKE:
        return new Like();
      default:
        return null;
    }
  }

}
