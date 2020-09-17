package com.social.backend.mapper.model.chat;

import java.util.Objects;

import com.social.backend.dto.chat.ChatDto;
import com.social.backend.dto.reply.MessageDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

public class MessageMapper extends AbstractMapper<Message> {

  private final AbstractMapper<User> userMapper;
  private final AbstractMapper<Chat> chatMapper;

  public MessageMapper(AbstractMapper<User> userMapper, AbstractMapper<Chat> chatMapper) {
    Objects.requireNonNull(userMapper, "User mapper must not be null");
    Objects.requireNonNull(chatMapper, "Chat mapper must not be null");
    this.userMapper = userMapper;
    this.chatMapper = chatMapper;
  }

  @Override
  public <R> R map(Message model, Class<R> dtoType) {
    logger.debug("Mapping Message to MessageDto by default");
    MessageDto dto = new MessageDto();
    dto.setId(model.getId());
    dto.setCreatedAt(model.getCreatedAt());
    dto.setUpdatedAt(model.getUpdatedAt());
    dto.setBody(model.getBody());
    dto.setAuthor(userMapper.map(model.getAuthor(), UserDto.class));
    dto.setChat(chatMapper.map(model.getChat(), ChatDto.class));
    return (R) dto;
  }

}
