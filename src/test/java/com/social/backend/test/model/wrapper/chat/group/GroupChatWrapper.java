package com.social.backend.test.model.wrapper.chat.group;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.test.model.mutator.ChatMutators;
import com.social.backend.test.model.wrapper.AbstractWrapper;

abstract class GroupChatWrapper extends AbstractWrapper<GroupChat> {

  GroupChatWrapper(Long id, String name) {
    super(new GroupChat());
    super
        .with(ChatMutators.id(id))
        .with(ChatMutators.name(name));
  }

}
