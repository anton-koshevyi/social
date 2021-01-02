package com.social.test.model.factory;

import java.util.HashMap;
import java.util.Map;

import com.social.model.chat.GroupChat;
import com.social.model.chat.Message;
import com.social.model.chat.PrivateChat;
import com.social.model.post.Comment;
import com.social.model.post.Post;
import com.social.model.user.User;

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
