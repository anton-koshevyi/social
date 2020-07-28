package com.social.backend.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.chat.CreateGroupDto;
import com.social.backend.dto.chat.UpdateGroupDto;
import com.social.backend.dto.chat.UpdateGroupMembersDto;
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
        User member = userService.findById(userId);
        return chatService.findAllByMember(member, pageable);
    }
    
    @GetMapping("/chats/{id}")
    public Chat chatByIdAndMember(@PathVariable Long id,
                                  @AuthenticationPrincipal(expression = "id") Long userId) {
        User member = userService.findById(userId);
        return chatService.findByIdAndMember(id, member);
    }
    
    @GetMapping("/chats/{id}/members")
    public Page<User> members(@PathVariable("id") Long id,
                              @AuthenticationPrincipal(expression = "id") Long userId,
                              Pageable pageable) {
        User member = userService.findById(userId);
        return chatService.getMembers(id, member, pageable);
    }
    
    @DeleteMapping("/chats/private/{id}")
    public void deletePrivate(@PathVariable("id") Long id,
                              @AuthenticationPrincipal(expression = "id") Long userId) {
        User user = userService.findById(userId);
        chatService.deletePrivate(id, user);
    }
    
    @PostMapping("/chats/group")
    public Chat group(@AuthenticationPrincipal(expression = "id") Long userId,
                      @Valid @RequestBody CreateGroupDto dto) {
        User creator = userService.findById(userId);
        String name = dto.getName();
        Set<User> members = findUsersByIds(dto.getMemberIds());
        return chatService.createGroup(creator, name, members);
    }
    
    @PatchMapping("/chats/group/{id}")
    public Chat updatedGroup(@PathVariable Long id,
                             @AuthenticationPrincipal(expression = "id") Long userId,
                             @Valid @RequestBody UpdateGroupDto dto) {
        // TODO: Implement as PATCH-request
        User member = userService.findById(userId);
        String name = dto.getName();
        return chatService.updateGroup(id, member, name);
    }
    
    @PutMapping("/chats/group/{id}")
    public void leaveGroup(@PathVariable("id") Long id,
                           @AuthenticationPrincipal(expression = "id") Long userId) {
        User user = userService.findById(userId);
        chatService.leaveGroup(id, user);
    }
    
    @DeleteMapping("/chats/group/{id}")
    public void deleteGroup(@PathVariable("id") Long id,
                            @AuthenticationPrincipal(expression = "id") Long userId) {
        User owner = userService.findById(userId);
        chatService.deleteGroup(id, owner);
    }
    
    @PutMapping("/chats/group/{id}/members")
    public Chat updatedGroupMembers(@PathVariable Long id,
                                    @AuthenticationPrincipal(expression = "id") Long userId,
                                    @Valid @RequestBody UpdateGroupMembersDto dto) {
        User member = userService.findById(userId);
        Set<User> members = findUsersByIds(dto.getMemberIds());
        return chatService.updateGroupMembers(id, member, members);
    }
    
    @PutMapping("/chats/group/{id}/members/{newOwnerId}")
    public Chat newOwner(@PathVariable Long id,
                         @AuthenticationPrincipal(expression = "id") Long userId,
                         @PathVariable Long newOwnerId) {
        User owner = userService.findById(userId);
        User newOwner = userService.findById(newOwnerId);
        return chatService.setOwner(id, owner, newOwner);
    }
    
    private Set<User> findUsersByIds(List<Long> ids) {
        return ids.stream()
                .map(userService::findById)
                .collect(Collectors.toSet());
    }
}
