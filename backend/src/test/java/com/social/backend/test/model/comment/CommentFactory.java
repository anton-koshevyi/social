package com.social.backend.test.model.comment;

import com.social.backend.model.post.Comment;
import com.social.backend.test.model.AbstractFactory;
import com.social.backend.test.model.ModelType;

public class CommentFactory extends AbstractFactory<Comment> {

  @Override
  public Comment createModel(ModelType<Comment> type) {
    switch (Enum.valueOf(CommentType.class, type.name())) {
      case BADLY:
        return new Badly().getModel();
      case LIKE:
        return new Like().getModel();
      case USEFUL:
        return new Useful().getModel();
      default:
        return null;
    }
  }

}
