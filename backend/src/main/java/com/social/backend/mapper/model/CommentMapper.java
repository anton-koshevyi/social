package com.social.backend.mapper.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.social.backend.dto.reply.CommentDto;
import com.social.backend.model.post.Comment;

@Mapper(uses = {UserMapper.class, PostMapper.class})
public interface CommentMapper {

  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  CommentDto toDto(Comment model);

}
