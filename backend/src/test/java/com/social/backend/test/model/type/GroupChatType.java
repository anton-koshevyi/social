package com.social.backend.test.model.type;

import com.social.backend.model.chat.GroupChat;

public enum GroupChatType implements ModelType<GroupChat> {

  RAW,
  CLASSMATES,
  SCIENTISTS,
  PARENTS;

  @Override
  public Class<GroupChat> modelClass() {
    return GroupChat.class;
  }

}
