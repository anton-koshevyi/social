package com.social.backend.test.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.social.backend.model.Reply;
import com.social.backend.model.user.User;

abstract class ReplyComparator<T extends Reply> implements Comparator<T> {

  private final Comparator<User> userComparator;

  protected ReplyComparator(Comparator<User> userComparator) {
    this.userComparator = userComparator;
  }

  @Override
  public int compare(T left, T right) {
    return new CompareToBuilder()
        .append(left.getId(), right.getId())
        .append(left.getBody(), right.getBody())
        .append(left.getAuthor(), right.getAuthor(), userComparator)
        .toComparison();
  }

}
