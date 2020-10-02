package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

class MessageComparator extends ReplyComparator<Message> {

  private final Comparator<Chat> chatComparator;

  MessageComparator(Comparator<User> userComparator, Comparator<Chat> chatComparator) {
    super(userComparator);
    this.chatComparator = chatComparator;
  }

  @Override
  public int compare(Message left, Message right) {
    int superCompare = super.compare(left, right);

    if (superCompare != 0) {
      return superCompare;
    }

    return ComparisonChain.start()
        .compare(left.getChat(), right.getChat(), chatComparator)
        .result();
  }

}
