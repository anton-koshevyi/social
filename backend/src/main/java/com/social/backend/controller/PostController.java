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

import com.social.backend.dto.post.ContentDto;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;

@RestController
public class PostController {
    private final PostService postService;
    private final UserService userService;
    
    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }
    
    @GetMapping("/posts")
    public Page<Post> all(Pageable pageable) {
        return postService.findAll(pageable);
    }
    
    @PostMapping("/posts")
    public Post created(@AuthenticationPrincipal(expression = "id") Long userId,
                        @Valid @RequestBody ContentDto dto) {
        User author = userService.findById(userId);
        String body = dto.getBody();
        return postService.create(author, body);
    }
    
    @GetMapping("/posts/{id}")
    public Post postById(@PathVariable Long id) {
        return postService.findById(id);
    }
    
    @PutMapping("/posts/{id}")
    public Post updated(@PathVariable Long id,
                        @AuthenticationPrincipal(expression = "id") Long userId,
                        @Valid @RequestBody ContentDto dto) {
        User author = userService.findById(userId);
        String body = dto.getBody();
        return postService.update(id, author, body);
    }
    
    @DeleteMapping("/posts/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal(expression = "id") Long userId) {
        User author = userService.findById(userId);
        postService.delete(id, author);
    }
}
