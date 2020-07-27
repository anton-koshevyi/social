package com.social.backend.controller;

import java.util.List;
import java.util.stream.Collectors;
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

import com.social.backend.dto.chat.GroupDto;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.UserService;

@RestController
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    
    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }
    
    @GetMapping("/chats")
    public Page<Chat> userAll(@AuthenticationPrincipal(expression = "id") Long userId,
                              Pageable pageable) {
        User user = userService.findById(userId);
        return chatService.findAllByUser(user, pageable);
    }
    
    @GetMapping("/chats/{id}")
    public Chat chatByIdAndUser(@PathVariable Long id,
                                @AuthenticationPrincipal(expression = "id") Long userId) {
        User user = userService.findById(userId);
        return chatService.findByIdAndUser(id, user);
    }
    
    @GetMapping("/chats/{id}/members")
    public Page<User> members(@PathVariable("id") Long id,
                              @AuthenticationPrincipal(expression = "id") Long userId,
                              Pageable pageable) {
        User member = userService.findById(userId);
        return chatService.getMembers(id, member, pageable);
    }
    
    @DeleteMapping("/chats/{id}/private")
    public void deletePrivate(@PathVariable("id") Long id,
                              @AuthenticationPrincipal(expression = "id") Long userId) {
        User user = userService.findById(userId);
        chatService.deletePrivate(id, user);
    }
    
    @PostMapping("/chats/group")
    public Chat groupChat(@AuthenticationPrincipal(expression = "id") Long userId,
                          @Valid @RequestBody GroupDto dto) {
        User creator = userService.findById(userId);
        String name = dto.getName();
        List<User> members = dto.getMemberIds()
                .stream()
                .map(userService::findById)
                .collect(Collectors.toList());
        return chatService.createGroup(creator, name, members);
    }
    
    @PutMapping("/chats/{id}/group")
    public Chat updateGroup(@PathVariable Long id,
                            @AuthenticationPrincipal(expression = "id") Long userId,
                            @Valid @RequestBody GroupDto dto) {
        User member = userService.findById(userId);
        String name = dto.getName();
        List<User> newMembers = dto.getMemberIds()
                .stream()
                .map(userService::findById)
                .collect(Collectors.toList());
        return chatService.updateGroup(id, member, name, newMembers);
    }
    
    @DeleteMapping("/chats/{id}/group")
    public void deleteGroup(@PathVariable("id") Long id,
                            @AuthenticationPrincipal(expression = "id") Long userId) {
        chatService.deleteGroup(id, userId);
    }
    
    // TODO: Set owner
    
    // @PutMapping("/chats/{id}/group/members/{userId}")
    // public Chat newOwner(@PathVariable Long id,
    //                         @AuthenticationPrincipal(expression = "id") Long userId,
    //                         @Valid @RequestBody GroupDto dto) {
    // }
    
    @PutMapping("/chats/{id}/group/members")
    public Chat removeMembers(@PathVariable("id") Long id,
                              @AuthenticationPrincipal(expression = "id") Long userId,
                              @Valid @RequestBody GroupDto dto) {
        List<User> members = dto.getMemberIds()
                .stream()
                .map(userService::findById)
                .collect(Collectors.toList());
        return chatService.removeGroupMembers(id, userId, members);
    }
    
    @PutMapping("/chats/{id}/group/leave")
    public void leaveGroup(@PathVariable("id") Long id,
                           @AuthenticationPrincipal(expression = "id") Long userId) {
        User user = userService.findById(userId);
        chatService.leaveGroup(id, user);
    }
}
