package com.social.backend.test.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

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
    return new CompareToBuilder()
        .appendSuper(super.compare(left, right))
        .append(left.getChat(), right.getChat(), chatComparator)
        .toComparison();
  }

}
