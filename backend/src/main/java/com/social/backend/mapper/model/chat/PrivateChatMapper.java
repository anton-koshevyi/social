package com.social.backend.mapper.model.chat;

import java.util.Objects;
import java.util.stream.Collectors;

import com.social.backend.dto.chat.PrivateChatDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

class PrivateChatMapper extends AbstractMapper<PrivateChat> {

  private final AbstractMapper<User> userMapper;

  PrivateChatMapper(AbstractMapper<User> userMapper) {
    Objects.requireNonNull(userMapper, "User mapper must not be null");
    this.userMapper = userMapper;
  }

  @Override
  public <R> R map(PrivateChat model, Class<R> dtoType) {
    logger.debug("Mapping PrivateChat to PrivateChatDto by default");
    PrivateChatDto dto = new PrivateChatDto();
    dto.setId(model.getId());
    dto.setType("private");
    dto.setMembers(model
        .getMembers()
        .stream()
        .map(user -> userMapper.map(user, UserDto.class))
        .collect(Collectors.toList()));
    return (R) dto;
  }

}
