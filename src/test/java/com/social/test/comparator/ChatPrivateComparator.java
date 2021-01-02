package com.social.test.comparator;

import java.util.Comparator;
import java.util.Set;

import com.social.model.chat.PrivateChat;
import com.social.model.user.User;

class ChatPrivateComparator extends ChatComparator<PrivateChat> {

  protected ChatPrivateComparator(Comparator<Set<User>> membersComparator) {
    super(membersComparator);
  }

}
