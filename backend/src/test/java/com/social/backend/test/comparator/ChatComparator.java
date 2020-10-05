package com.social.backend.test.comparator;

import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;

abstract class ChatComparator<T extends Chat> implements Comparator<T> {

  private final Comparator<Set<User>> membersComparator;

  protected ChatComparator(Comparator<User> userComparator) {
    this.membersComparator = new CollectionComparatorAdapter<>(userComparator);
  }

  @Override
  public int compare(T left, T right) {
    return new CompareToBuilder()
        .append(left.getId(), right.getId())
        .append(left.getMembers(), right.getMembers(), membersComparator)
        .toComparison();
  }

}
