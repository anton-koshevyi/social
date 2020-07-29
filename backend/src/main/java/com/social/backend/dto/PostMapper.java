package com.social.backend.dto;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.social.backend.dto.post.PostDto;
import com.social.backend.model.post.Post;

@Component
public class PostMapper implements ResponseMapper<Post, PostDto> {
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
                .setComments(source.getComments().size());
    }
}
