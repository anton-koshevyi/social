package com.social.backend.test.model.factory;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.test.model.type.GroupChatType;
import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.wrapper.AbstractWrapper;
import com.social.backend.test.model.wrapper.ModelWrapper;
import com.social.backend.test.model.wrapper.chat.group.Classmates;
import com.social.backend.test.model.wrapper.chat.group.Parents;
import com.social.backend.test.model.wrapper.chat.group.Scientists;

class GroupChatFactory extends AbstractFactory<GroupChat> {

  @Override
  ModelWrapper<GroupChat> createWrapper(ModelType<GroupChat> type) {
    switch (Enum.valueOf(GroupChatType.class, type.name())) {
      case RAW:
        return new AbstractWrapper<GroupChat>(new GroupChat()) {};
      case CLASSMATES:
        return new Classmates();
      case PARENTS:
        return new Parents();
      case SCIENTISTS:
        return new Scientists();
      default:
        return null;
    }
  }

}
