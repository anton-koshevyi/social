package com.social.backend.dto.reply;

import com.social.backend.dto.post.PostDto;

public class CommentDto extends ReplyDto {
    private PostDto post;
    
    public CommentDto setPost(PostDto post) {
        this.post = post;
        return this;
    }
    
    public PostDto getPost() {
        return post;
    }
}
