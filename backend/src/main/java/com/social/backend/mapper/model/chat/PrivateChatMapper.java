package com.social.backend.mapper.model.chat;

import java.util.Objects;
import java.util.stream.Collectors;

import com.social.backend.dto.chat.PrivateDto;
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
    logger.debug("Mapping PrivateChat to PrivateDto by default");
    PrivateDto dto = new PrivateDto();
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
