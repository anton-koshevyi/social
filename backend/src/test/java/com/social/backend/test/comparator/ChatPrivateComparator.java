package com.social.backend.test.comparator;

import java.util.Comparator;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

class ChatPrivateComparator extends ChatComparator<PrivateChat> {

  ChatPrivateComparator(Comparator<User> userComparator) {
    super(userComparator);
  }

}
