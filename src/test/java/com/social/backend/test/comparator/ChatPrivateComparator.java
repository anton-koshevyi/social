package com.social.backend.test.comparator;

import java.util.Comparator;
import java.util.Set;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

class ChatPrivateComparator extends ChatComparator<PrivateChat> {

  protected ChatPrivateComparator(Comparator<Set<User>> membersComparator) {
    super(membersComparator);
  }

}
