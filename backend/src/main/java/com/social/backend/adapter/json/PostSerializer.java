package com.social.backend.adapter.json;

import java.time.ZonedDateTime;

import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.post.PostDto;
import com.social.backend.model.post.Post;

@JsonComponent
public class PostSerializer
        extends AbstractSerializer<Post>
        implements EntityMapper<Post, PostDto> {
    @Override
    public Object beforeSerialize(Post post) {
        return this.map(post);
    }
    
    @Override
    public PostDto map(Post source) {
        if (source == null) {
            return null;
        }
    
        ZonedDateTime updateDate = source.getUpdated();
        PostDto dto = new PostDto();
        dto.setId(source.getId());
        dto.setCreationDate(source.getCreated());
        dto.setUpdateDate(updateDate);
        dto.setUpdated(updateDate != null);
        dto.setBody(source.getBody());
        dto.setComments(source.getComments().size());
        dto.setAuthor(source.getAuthor());
        return dto;
    }
}
