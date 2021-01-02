package com.social.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.social.dto.reply.MessageDto;
import com.social.model.chat.Message;

@Mapper(uses = {UserMapper.class, ChatMapper.class})
public interface MessageMapper {

  MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

  MessageDto toDto(Message model);

}
