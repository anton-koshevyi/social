package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

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
    int superCompare = super.compare(left, right);

    if (superCompare != 0) {
      return superCompare;
    }

    return ComparisonChain.start()
        .compare(left.getPost(), right.getPost(), postComparator)
        .result();
  }

}
