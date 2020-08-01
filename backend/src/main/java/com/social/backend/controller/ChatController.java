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

import com.social.backend.dto.chat.GroupCreateDto;
import com.social.backend.dto.chat.GroupMembersDto;
import com.social.backend.dto.chat.GroupUpdateDto;
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
  public Page<Chat> getAll(@AuthenticationPrincipal(expression = "id") Long userId,
                           Pageable pageable) {
    User member = userService.find(userId);
    return chatService.findAll(member, pageable);
  }
  
  @GetMapping("/chats/{id}")
  public Chat get(@PathVariable Long id,
                  @AuthenticationPrincipal(expression = "id") Long userId) {
    User member = userService.find(userId);
    return chatService.find(id, member);
  }
  
  @GetMapping("/chats/{id}/members")
  public Page<User> getMembers(@PathVariable("id") Long id,
                               @AuthenticationPrincipal(expression = "id") Long userId,
                               Pageable pageable) {
    User member = userService.find(userId);
    return chatService.getMembers(id, member, pageable);
  }
  
  @DeleteMapping("/chats/private/{id}")
  public void deletePrivate(@PathVariable("id") Long id,
                            @AuthenticationPrincipal(expression = "id") Long userId) {
    User user = userService.find(userId);
    chatService.deletePrivate(id, user);
  }
  
  @PostMapping("/chats/group")
  public Chat createGroup(@AuthenticationPrincipal(expression = "id") Long userId,
                          @Valid @RequestBody GroupCreateDto dto) {
    User creator = userService.find(userId);
    String name = dto.getName();
    Set<User> members = findUsersByIds(dto.getMemberIds());
    return chatService.createGroup(creator, name, members);
  }
  
  @PatchMapping("/chats/group/{id}")
  public Chat updateGroup(@PathVariable Long id,
                          @AuthenticationPrincipal(expression = "id") Long userId,
                          @Valid @RequestBody GroupUpdateDto dto) {
    // TODO: Implement as PATCH-request
    User member = userService.find(userId);
    String name = dto.getName();
    return chatService.updateGroup(id, member, name);
  }
  
  @PutMapping("/chats/group/{id}")
  public void leaveGroup(@PathVariable("id") Long id,
                         @AuthenticationPrincipal(expression = "id") Long userId) {
    User user = userService.find(userId);
    chatService.leaveGroup(id, user);
  }
  
  @DeleteMapping("/chats/group/{id}")
  public void deleteGroup(@PathVariable("id") Long id,
                          @AuthenticationPrincipal(expression = "id") Long userId) {
    User owner = userService.find(userId);
    chatService.deleteGroup(id, owner);
  }
  
  @PutMapping("/chats/group/{id}/members")
  public Chat updateGroupMembers(@PathVariable Long id,
                                 @AuthenticationPrincipal(expression = "id") Long userId,
                                 @Valid @RequestBody GroupMembersDto dto) {
    User member = userService.find(userId);
    Set<User> members = findUsersByIds(dto.getMemberIds());
    return chatService.updateGroupMembers(id, member, members);
  }
  
  @PutMapping("/chats/group/{id}/members/{newOwnerId}")
  public Chat changeOwner(@PathVariable Long id,
                          @AuthenticationPrincipal(expression = "id") Long userId,
                          @PathVariable Long newOwnerId) {
    User owner = userService.find(userId);
    User newOwner = userService.find(newOwnerId);
    return chatService.changeOwner(id, owner, newOwner);
  }
  
  private Set<User> findUsersByIds(List<Long> ids) {
    return ids.stream()
        .map(userService::find)
        .collect(Collectors.toSet());
  }
  
}
