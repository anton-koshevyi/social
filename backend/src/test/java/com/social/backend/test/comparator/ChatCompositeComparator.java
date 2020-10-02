package com.social.backend.test.comparator;

import java.util.Comparator;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;

class ChatCompositeComparator implements Comparator<Chat> {

  private final Comparator<PrivateChat> privateComparator;
  private final Comparator<GroupChat> groupComparator;

  ChatCompositeComparator(Comparator<PrivateChat> privateComparator,
                          Comparator<GroupChat> groupComparator) {
    this.privateComparator = privateComparator;
    this.groupComparator = groupComparator;
  }

  public int compare(Chat left, Chat right) {
    if (left instanceof PrivateChat) {
      return privateComparator.compare((PrivateChat) left, (PrivateChat) right);
    }

    if (left instanceof GroupChat) {
      return groupComparator.compare((GroupChat) left, (GroupChat) right);
    }

    throw new IllegalStateException("Unsupported chat type: " + left);
  }

}
