package com.social.test.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.social.model.post.Post;
import com.social.model.user.User;

class PostComparator implements Comparator<Post> {

  private final Comparator<User> userComparator;

  PostComparator(Comparator<User> userComparator) {
    this.userComparator = userComparator;
  }

  @Override
  public int compare(Post left, Post right) {
    return new CompareToBuilder()
        .append(left.getId(), right.getId())
        .append(left.getTitle(), right.getTitle())
        .append(left.getBody(), right.getBody())
        .append(left.getAuthor(), right.getAuthor(), userComparator)
        .toComparison();
  }

}
