package com.social.test.model.wrapper.chat.group;

import com.social.model.chat.GroupChat;
import com.social.test.model.mutator.ChatMutators;
import com.social.test.model.wrapper.AbstractWrapper;

abstract class GroupChatWrapper extends AbstractWrapper<GroupChat> {

  GroupChatWrapper(Long id, String name) {
    super(new GroupChat());
    super
        .with(ChatMutators.id(id))
        .with(ChatMutators.name(name));
  }

}
