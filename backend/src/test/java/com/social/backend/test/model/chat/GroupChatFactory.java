package com.social.backend.test.model.chat;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.test.model.AbstractFactory;
import com.social.backend.test.model.ModelType;

public class GroupChatFactory extends AbstractFactory<GroupChat> {

  @Override
  public GroupChat createModel(ModelType<GroupChat> type) {
    switch (Enum.valueOf(GroupChatType.class, type.name())) {
      case CLASSMATES:
        return new Classmates().getModel();
      case PARENTS:
        return new Parents().getModel();
      case SCIENTISTS:
        return new Scientists().getModel();
      default:
        return null;
    }
  }

}
