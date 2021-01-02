package com.social.test.model.type;

import com.social.model.chat.GroupChat;

public enum GroupChatType implements ModelType<GroupChat> {

  RAW,
  CLASSMATES,
  SCIENTISTS;

  @Override
  public Class<GroupChat> modelClass() {
    return GroupChat.class;
  }

}
