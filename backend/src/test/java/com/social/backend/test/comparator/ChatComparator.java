package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

class ChatComparator implements Comparator<Chat> {

  private final Comparator<User> userComparator;

  ChatComparator(Comparator<User> userComparator) {
    this.userComparator = userComparator;
  }

  @Override
  public int compare(Chat left, Chat right) {
    int commonCompare = ComparisonChain.start()
        .compare(left.getId(), right.getId())
        .result();

    if (commonCompare != 0) {
      return commonCompare;
    }

    if ((left instanceof PrivateChat)) {
      return commonCompare;
    }

    if ((left instanceof GroupChat)) {
      return compare((GroupChat) left, (GroupChat) right);
    }

    throw new IllegalStateException("Unsupported chat type");
  }

  private int compare(GroupChat left, GroupChat right) {
    return ComparisonChain.start()
        .compare(left.getName(), right.getName())
        .compare(left.getOwner(), right.getOwner(), userComparator)
        .result();
  }

}
