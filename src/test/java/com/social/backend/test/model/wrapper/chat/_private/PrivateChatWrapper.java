package com.social.backend.test.model.wrapper.chat._private;

import com.social.backend.model.chat.PrivateChat;
import com.social.backend.test.model.mutator.ChatMutators;
import com.social.backend.test.model.wrapper.AbstractWrapper;

abstract class PrivateChatWrapper extends AbstractWrapper<PrivateChat> {

  protected PrivateChatWrapper(Long id) {
    super(new PrivateChat());
    super
        .with(ChatMutators.id(id));
  }

}
