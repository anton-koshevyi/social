package com.social.backend.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.user.RoleDto;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;

@RestController
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final ChatService chatService;
    
    @Autowired
    public UserController(UserService userService,
                          PostService postService, ChatService chatService) {
        this.userService = userService;
        this.postService = postService;
        this.chatService = chatService;
    }
    
    @GetMapping("/users")
    public Page<User> getAll(Pageable pageable) {
        return userService.findAll(pageable);
    }
    
    @GetMapping("/users/{id}")
    public User get(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @PatchMapping("/users/{id}/roles")
    public User updateRole(@PathVariable Long id,
                           @Valid @RequestBody RoleDto dto) {
        // TODO: Implement as PATCH-request
        Boolean moder = dto.getModer();
        return userService.updateRole(id, moder);
    }
    
    @GetMapping("/users/{id}/friends")
    public Page<User> getFriends(@PathVariable Long id,
                                 Pageable pageable) {
        return userService.getFriends(id, pageable);
    }
    
    @PostMapping("/users/{id}/friends")
    public void addFriend(@AuthenticationPrincipal(expression = "id") Long id,
                          @PathVariable("id") Long targetId) {
        userService.addFriend(id, targetId);
    }
    
    @DeleteMapping("/users/{id}/friends")
    public void removeFriend(@AuthenticationPrincipal(expression = "id") Long id,
                             @PathVariable("id") Long targetId) {
        userService.removeFriend(id, targetId);
    }
    
    @GetMapping("/users/{id}/posts")
    public Page<Post> getPosts(@PathVariable Long id,
                               Pageable pageable) {
        User author = userService.findById(id);
        return postService.findAllByAuthor(author, pageable);
    }
    
    @PostMapping("/users/{id}/chats/private")
    public Chat createPrivateChat(@AuthenticationPrincipal(expression = "id") Long userId,
                                  @PathVariable("id") Long targetId) {
        User user = userService.findById(userId);
        User target = userService.findById(targetId);
        return chatService.createPrivate(user, target);
    }
}
