package com.social.backend.mapper.model.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

/**
 * Delegates model mapping to specific chat mapper depending on model type.
 * Stores mappers according to lazy Flyweight pattern, but to non-static map.
 */
public class ChatMapper extends AbstractMapper<Chat> {

  private final Map<String, AbstractMapper<? extends Chat>> chatMappers = new HashMap<>();
  private final AbstractMapper<User> userMapper;

  public ChatMapper(AbstractMapper<User> userMapper) {
    Objects.requireNonNull(userMapper, "User mapper must not be null");
    this.userMapper = userMapper;
  }

  @Override
  public <R> R toDto(Chat model, Class<R> dtoType) {
    if (model == null) {
      logger.debug("Mapped to {} model is null", dtoType);
      return null;
    }

    String key = model.getClass().getName();

    if (!chatMappers.containsKey(key)) {
      if (model instanceof PrivateChat) {
        logger.debug("Creating PrivateChatMapper for key '{}'", key);
        chatMappers.put(key, new PrivateChatMapper(userMapper));
      }

      if (model instanceof GroupChat) {
        logger.debug("Creating GroupChatMapper for key '{}'", key);
        chatMappers.put(key, new GroupChatMapper(userMapper));
      }
    }

    AbstractMapper<Chat> mapper = (AbstractMapper<Chat>) chatMappers.get(key);
    logger.debug("{} ChatMapper for key '{}'", (mapper == null) ? "No" : "Obtained", key);
    return (mapper == null) ? null : mapper.toDto(model, dtoType);
  }

}
