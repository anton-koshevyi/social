package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.User;

class ChatGroupComparator extends ChatComparator<GroupChat> {

  private final Comparator<User> userComparator;

  ChatGroupComparator(Comparator<User> userComparator) {
    super(userComparator);
    this.userComparator = userComparator;
  }

  @Override
  public int compare(GroupChat left, GroupChat right) {
    int superCompare = super.compare(left, right);

    if (superCompare != 0) {
      return superCompare;
    }

    return ComparisonChain.start()
        .compare(left.getName(), right.getName())
        .compare(left.getOwner(), right.getOwner(), userComparator)
        .result();
  }

}
