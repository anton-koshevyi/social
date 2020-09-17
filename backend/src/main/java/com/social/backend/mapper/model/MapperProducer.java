package com.social.backend.mapper.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.social.backend.mapper.model.chat.ChatMapper;
import com.social.backend.mapper.model.chat.MessageMapper;
import com.social.backend.mapper.model.post.CommentMapper;
import com.social.backend.mapper.model.post.PostMapper;
import com.social.backend.mapper.model.user.UserMapper;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public final class MapperProducer {

  private static final Logger logger = LoggerFactory.getLogger(MapperProducer.class);
  private static final Map<String, AbstractMapper<?>> typeMappers = new HashMap<>();

  private MapperProducer() {
  }

  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
  public static <T> AbstractMapper<T> getMapper(Class<T> modelType) {
    if (modelType == null) {
      logger.debug("Model type is null");
      return null;
    }

    String key = modelType.getName();

    if (!typeMappers.containsKey(key)) {
      if (User.class.equals(modelType)) {
        logger.debug("Creating UserMapper for key '{}'", key);
        typeMappers.put(key, new UserMapper());
      }

      if (Post.class.equals(modelType)) {
        logger.debug("Creating PostMapper for key '{}'", key);
        typeMappers.put(key, new PostMapper(
            MapperProducer.getMapper(User.class)
        ));
      }

      if (Comment.class.equals(modelType)) {
        logger.debug("Creating CommentMapper for key '{}'", key);
        typeMappers.put(key, new CommentMapper(
            MapperProducer.getMapper(User.class),
            MapperProducer.getMapper(Post.class)
        ));
      }

      if (Chat.class.isAssignableFrom(modelType)) {
        String chatKey = Chat.class.getName();

        if (!typeMappers.containsKey(chatKey)) {
          logger.debug("Creating ChatMapper for key '{}'", chatKey);
          typeMappers.put(chatKey, new ChatMapper(
              MapperProducer.getMapper(User.class)
          ));
        }

        logger.debug("Creating ChatMapper for key '{}'", key);
        typeMappers.put(key, MapperProducer.getMapper(Chat.class));
      }

      if (Message.class.equals(modelType)) {
        logger.debug("Creating MessageMapper for key '{}'", key);
        typeMappers.put(key, new MessageMapper(
            MapperProducer.getMapper(User.class),
            MapperProducer.getMapper(Chat.class)
        ));
      }
    }

    return (AbstractMapper<T>) typeMappers.get(key);
  }

}
