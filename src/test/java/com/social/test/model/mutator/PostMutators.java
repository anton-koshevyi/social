package com.social.test.model.mutator;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.function.Consumer;

import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;

public final class PostMutators {

  private PostMutators() {
  }

  public static Consumer<Post> id(Long v) {
    return m -> m.setId(v);
  }

  public static Consumer<Post> createdAt(ZonedDateTime v) {
    return m -> m.setCreatedAt(v);
  }

  public static Consumer<Post> updatedAt(ZonedDateTime v) {
    return m -> m.setUpdatedAt(v);
  }

  public static Consumer<Post> title(String v) {
    return m -> m.setTitle(v);
  }

  public static Consumer<Post> body(String v) {
    return m -> m.setBody(v);
  }

  public static Consumer<Post> comments(Comment... v) {
    return m -> m.setComments(Arrays.asList(v));
  }

  public static Consumer<Post> author(User v) {
    return m -> m.setAuthor(v);
  }

}
