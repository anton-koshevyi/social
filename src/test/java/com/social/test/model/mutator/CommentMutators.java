package com.social.test.model.mutator;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;

public final class CommentMutators {

  private CommentMutators() {
  }

  public static Consumer<Comment> id(Long v) {
    return m -> m.setId(v);
  }

  public static Consumer<Comment> createdAt(ZonedDateTime v) {
    return m -> m.setCreatedAt(v);
  }

  public static Consumer<Comment> updatedAt(ZonedDateTime v) {
    return m -> m.setUpdatedAt(v);
  }

  public static Consumer<Comment> body(String v) {
    return m -> m.setBody(v);
  }

  public static Consumer<Comment> author(User v) {
    return m -> m.setAuthor(v);
  }

  public static Consumer<Comment> post(Post v) {
    return m -> m.setPost(v);
  }

}
