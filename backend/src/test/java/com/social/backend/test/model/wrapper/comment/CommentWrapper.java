package com.social.backend.test.model.wrapper.comment;

import com.social.backend.model.post.Comment;
import com.social.backend.test.model.mutator.CommentMutators;
import com.social.backend.test.model.wrapper.AbstractWrapper;

abstract class CommentWrapper extends AbstractWrapper<Comment> {

  CommentWrapper(Long id, String body) {
    super(new Comment());
    super
        .with(CommentMutators.id(id))
        .with(CommentMutators.body(body));
  }

}
