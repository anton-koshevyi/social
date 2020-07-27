package com.social.backend.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.reply.ContentDto;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.service.CommentService;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    
    @Autowired
    public CommentController(CommentService commentService,
                             PostService postService,
                             UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }
    
    @GetMapping("/post/{postId}/comments")
    public Page<Comment> postAll(@PathVariable Long postId,
                                 Pageable pageable) {
        return commentService.findAllByPostId(postId, pageable);
    }
    
    @PostMapping("/post/{postId}/comments")
    public Comment created(@PathVariable Long postId,
                           @AuthenticationPrincipal(expression = "id") Long userId,
                           @Valid @RequestBody ContentDto dto) {
        Post post = postService.findById(postId);
        User author = userService.findById(userId);
        String body = dto.getBody();
        return commentService.create(post, author, body);
    }
    
    @PutMapping("/post/{postId}/comments/{id}")
    public Comment updated(@PathVariable Long id,
                           @AuthenticationPrincipal(expression = "id") Long userId,
                           @Valid @RequestBody ContentDto dto) {
        String body = dto.getBody();
        return commentService.update(id, userId, body);
    }
    
    @DeleteMapping("/post/{postId}/comments/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal(expression = "id") Long userId) {
        commentService.delete(id, userId);
    }
}
