package com.social.test.model.wrapper.message;

import com.social.model.chat.Message;
import com.social.test.model.mutator.MessageMutators;
import com.social.test.model.wrapper.AbstractWrapper;

abstract class MessageWrapper extends AbstractWrapper<Message> {

  MessageWrapper(Long id, String body) {
    super(new Message());
    super
        .with(MessageMutators.id(id))
        .with(MessageMutators.body(body));
  }

}
