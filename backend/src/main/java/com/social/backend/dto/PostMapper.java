package com.social.backend.dto;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@Component
public class PostMapper implements ResponseMapper<Post, PostDto> {
    private final ResponseMapper<User, UserDto> userMapper;
    
    @Autowired
    public PostMapper(ResponseMapper<User, UserDto> userMapper) {
        this.userMapper = userMapper;
    }
    
    @Override
    public PostDto map(Post source) {
        if (source == null) {
            return null;
        }
        
        ZonedDateTime updateDate = source.getUpdated();
        UserDto author = userMapper.map(source.getAuthor());
        return new PostDto()
                .setId(source.getId())
                .setCreationDate(source.getCreated())
                .setUpdateDate(updateDate)
                .setUpdated(updateDate != null)
                .setBody(source.getBody())
                .setAuthor(author)
                .setComments(source.getComments().size());
    }
    
    @Override
    public PostDto mapHidden(Post source) {
        UserDto hiddenAuthor = userMapper.mapHidden(source.getAuthor());
        return this.map(source)
                .setAuthor(hiddenAuthor);
    }
    
    @Override
    public PostDto mapExtended(Post source) {
        UserDto extendedAuthor = userMapper.mapExtended(source.getAuthor());
        return this.map(source)
                .setAuthor(extendedAuthor);
    }
}
