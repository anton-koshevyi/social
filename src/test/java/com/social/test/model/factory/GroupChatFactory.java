package com.social.test.model.factory;

import com.social.model.chat.GroupChat;
import com.social.test.model.type.GroupChatType;
import com.social.test.model.type.ModelType;
import com.social.test.model.wrapper.AbstractWrapper;
import com.social.test.model.wrapper.ModelWrapper;
import com.social.test.model.wrapper.chat.group.Classmates;
import com.social.test.model.wrapper.chat.group.Scientists;

class GroupChatFactory extends AbstractFactory<GroupChat> {

  @Override
  ModelWrapper<GroupChat> createWrapper(ModelType<GroupChat> type) {
    switch (Enum.valueOf(GroupChatType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<GroupChat>(new GroupChat()) {
        };
      case CLASSMATES:
        return new Classmates();
      case SCIENTISTS:
        return new Scientists();
      default:
        return null;
    }
  }

}
