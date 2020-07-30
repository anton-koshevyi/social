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
        return new PostDto()
                .setId(source.getId())
                .setCreationDate(source.getCreated())
                .setUpdateDate(updateDate)
                .setUpdated(updateDate != null)
                .setBody(source.getBody())
                .setComments(source.getComments().size())
                .setAuthor(source.getAuthor());
    }
}
