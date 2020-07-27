package com.social.backend.dto.reply;

import com.social.backend.model.post.Post;

public class ResponseCommentDto extends ResponseDto {
    private Post post;
    
    public ResponseCommentDto setPost(Post post) {
        this.post = post;
        return this;
    }
    
    public Post getPost() {
        return post;
    }
}
