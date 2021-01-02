package com.social.test.model.factory;

import com.social.model.post.Comment;
import com.social.test.model.type.CommentType;
import com.social.test.model.type.ModelType;
import com.social.test.model.wrapper.AbstractWrapper;
import com.social.test.model.wrapper.ModelWrapper;
import com.social.test.model.wrapper.comment.Badly;
import com.social.test.model.wrapper.comment.Like;

class CommentFactory extends AbstractFactory<Comment> {

  @Override
  ModelWrapper<Comment> createWrapper(ModelType<Comment> type) {
    switch (Enum.valueOf(CommentType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<Comment>(new Comment()) {
        };
      case BADLY:
        return new Badly();
      case LIKE:
        return new Like();
      default:
        return null;
    }
  }

}
