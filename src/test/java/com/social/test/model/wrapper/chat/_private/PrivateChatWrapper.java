package com.social.test.model.wrapper.chat._private;

import com.social.model.chat.PrivateChat;
import com.social.test.model.mutator.ChatMutators;
import com.social.test.model.wrapper.AbstractWrapper;

abstract class PrivateChatWrapper extends AbstractWrapper<PrivateChat> {

  protected PrivateChatWrapper(Long id) {
    super(new PrivateChat());
    super
        .with(ChatMutators.id(id));
  }

}
