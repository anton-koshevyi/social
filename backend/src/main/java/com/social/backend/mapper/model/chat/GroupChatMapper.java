package com.social.backend.mapper.model.chat;

import java.util.Objects;

import com.social.backend.dto.chat.GroupDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.User;

class GroupChatMapper extends AbstractMapper<GroupChat> {

  private final AbstractMapper<User> userMapper;

  GroupChatMapper(AbstractMapper<User> userMapper) {
    Objects.requireNonNull(userMapper, "User mapper must not be null");
    this.userMapper = userMapper;
  }

  @Override
  public <R> R toDto(GroupChat model, Class<R> dtoType) {
    logger.debug("Mapping model to GroupDto by default");
    GroupDto dto = new GroupDto();
    dto.setId(model.getId());
    dto.setType("group");
    dto.setName(model.getName());
    dto.setMembers(model.getMembers().size());
    dto.setOwner(userMapper.toDto(model.getOwner(), UserDto.class));
    return (R) dto;
  }

}
