package com.social.backend.test.model.chat;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.test.model.ModelType;

public enum GroupChatType implements ModelType<GroupChat> {

  CLASSMATES,
  PARENTS,
  SCIENTISTS;

  @Override
  public Class<GroupChat> modelClass() {
    return GroupChat.class;
  }

}
