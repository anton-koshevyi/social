package com.social.backend.test.comparator;

import java.util.Comparator;
import java.util.Set;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;

abstract class ChatComparator<T extends Chat> implements Comparator<T> {

  private final Comparator<Set<User>> membersComparator;

  protected ChatComparator(Comparator<User> userComparator) {
    this.membersComparator = new CollectionComparatorAdapter<>(userComparator);
  }

  @Override
  public int compare(T left, T right) {
    return ComparisonChain.start()
        .compare(left.getId(), right.getId())
        .compare(left.getMembers(), right.getMembers(), membersComparator)
        .result();
  }

}
