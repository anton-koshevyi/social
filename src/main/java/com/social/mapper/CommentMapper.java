package com.social.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.social.dto.reply.CommentDto;
import com.social.model.post.Comment;

@Mapper(uses = {UserMapper.class, PostMapper.class})
public interface CommentMapper {

  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  CommentDto toDto(Comment model);

}
