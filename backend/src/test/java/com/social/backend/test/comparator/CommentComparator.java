package com.social.backend.test.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

class CommentComparator extends ReplyComparator<Comment> {

  private final Comparator<Post> postComparator;

  CommentComparator(Comparator<User> userComparator, Comparator<Post> postComparator) {
    super(userComparator);
    this.postComparator = postComparator;
  }

  @Override
  public int compare(Comment left, Comment right) {
    return new CompareToBuilder()
        .appendSuper(super.compare(left, right))
        .append(left.getPost(), right.getPost(), postComparator)
        .toComparison();
  }

}
