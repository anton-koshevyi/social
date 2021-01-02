package com.social.backend.test.model.factory;

import java.util.HashMap;
import java.util.Map;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

final class FactoryProducer {

  private static final Map<String, AbstractFactory<?>> typeFactories = new HashMap<>();

  private FactoryProducer() {
  }

  static <T> AbstractFactory<T> getFactory(Class<T> type) {
    String typeName = type.getName();

    if (!typeFactories.containsKey(typeName)) {
      if (User.class.equals(type)) {
        typeFactories.put(typeName, new UserFactory());
      }

      if (Post.class.equals(type)) {
        typeFactories.put(typeName, new PostFactory());
      }

      if (Comment.class.equals(type)) {
        typeFactories.put(typeName, new CommentFactory());
      }

      if (PrivateChat.class.equals(type)) {
        typeFactories.put(typeName, new PrivateChatFactory());
      }

      if (GroupChat.class.equals(type)) {
        typeFactories.put(typeName, new GroupChatFactory());
      }

      if (Message.class.equals(type)) {
        typeFactories.put(typeName, new MessageFactory());
      }
    }

    return (AbstractFactory<T>) typeFactories.get(typeName);
  }

}
