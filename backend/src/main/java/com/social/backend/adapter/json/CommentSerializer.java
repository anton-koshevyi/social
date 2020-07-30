package com.social.backend.adapter.json;

import java.time.ZonedDateTime;

import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.reply.CommentDto;
import com.social.backend.model.post.Comment;

@JsonComponent
public class CommentSerializer
        extends AbstractSerializer<Comment>
        implements EntityMapper<Comment, CommentDto> {
    @Override
    public Object beforeSerialize(Comment comment) {
        return this.map(comment);
    }
    
    @Override
    public CommentDto map(Comment source) {
        if (source == null) {
            return null;
        }
    
        ZonedDateTime updateDate = source.getUpdated();
        CommentDto dto = new CommentDto();
        dto.setId(source.getId());
        dto.setCreationDate(source.getCreated());
        dto.setUpdateDate(updateDate);
        dto.setUpdated(updateDate != null);
        dto.setBody(source.getBody());
        dto.setPost(source.getPost());
        dto.setAuthor(source.getAuthor());
        return dto;
    }
}
