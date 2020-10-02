package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.Reply;
import com.social.backend.model.user.User;

abstract class ReplyComparator<T extends Reply> implements Comparator<T> {

  private final Comparator<User> userComparator;

  protected ReplyComparator(Comparator<User> userComparator) {
    this.userComparator = userComparator;
  }

  @Override
  public int compare(T left, T right) {
    return ComparisonChain.start()
        .compare(left.getId(), right.getId())
        .compare(left.getCreatedAt(), right.getCreatedAt(), NotNullComparator.leftNotNull())
        .compare(left.getBody(), right.getBody())
        .compare(left.getAuthor(), right.getAuthor(), userComparator)
        .result();
  }

}
