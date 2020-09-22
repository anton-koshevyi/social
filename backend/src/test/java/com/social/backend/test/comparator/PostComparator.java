package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

class PostComparator implements Comparator<Post> {

  private final Comparator<User> userComparator;

  PostComparator(Comparator<User> userComparator) {
    this.userComparator = userComparator;
  }

  @Override
  public int compare(Post left, Post right) {
    return ComparisonChain.start()
        .compare(left.getId(), right.getId())
        .compare(left.getCreatedAt(), right.getCreatedAt(), NotNullComparator.leftNotNull())
        .compare(left.getTitle(), right.getTitle())
        .compare(left.getBody(), right.getBody())
        .compare(left.getAuthor(), right.getAuthor(), userComparator)
        .result();
  }

}
