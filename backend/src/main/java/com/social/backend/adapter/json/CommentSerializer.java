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
        return (CommentDto) new CommentDto()
                .setPost(source.getPost())
                .setAuthor(source.getAuthor())
                .setId(source.getId())
                .setCreationDate(source.getCreated())
                .setUpdateDate(updateDate)
                .setUpdated(updateDate != null)
                .setBody(source.getBody());
    }
}
