package com.social.backend.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.common.PrincipalHolder;
import com.social.backend.dto.chat.ChatDto;
import com.social.backend.dto.chat.GroupChatDto;
import com.social.backend.dto.chat.GroupCreateDto;
import com.social.backend.dto.chat.GroupMembersDto;
import com.social.backend.dto.chat.GroupUpdateDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.ChatMapper;
import com.social.backend.mapper.UserMapper;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
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
  public Page<ChatDto> getAll(Pageable pageable) {
    User member = userService.find(PrincipalHolder.getUserId());
    Page<Chat> chats = chatService.findAll(member, pageable);
    return chats.map(ChatMapper.INSTANCE::toDto);
  }

  @GetMapping("/chats/{id}")
  public ChatDto get(@PathVariable Long id) {
    User member = userService.find(PrincipalHolder.getUserId());
    Chat chat = chatService.find(id, member);
    return ChatMapper.INSTANCE.toDto(chat);
  }

  @GetMapping("/chats/{id}/members")
  public Page<UserDto> getMembers(@PathVariable("id") Long id,
                                  Pageable pageable) {
    User member = userService.find(PrincipalHolder.getUserId());
    Page<User> users = chatService.getMembers(id, member, pageable);
    return users.map(UserMapper.INSTANCE::toDto);
  }

  @DeleteMapping("/chats/private/{id}")
  public void deletePrivate(@PathVariable("id") Long id) {
    User member = userService.find(PrincipalHolder.getUserId());
    chatService.deletePrivate(id, member);
  }

  @PostMapping("/chats/group")
  public GroupChatDto createGroup(@Valid @RequestBody GroupCreateDto dto) {
    User creator = userService.find(PrincipalHolder.getUserId());
    Set<User> members = findUsersByIds(dto.getMembers());
    GroupChat chat = chatService.createGroup(
        creator,
        dto.getName(),
        members
    );
    return ChatMapper.INSTANCE.toDto(chat);
  }

  @PatchMapping("/chats/group/{id}")
  public GroupChatDto updateGroup(@PathVariable Long id,
                                  @Valid @RequestBody GroupUpdateDto dto) {
    User member = userService.find(PrincipalHolder.getUserId());
    GroupChat chat = chatService.updateGroup(
        id,
        member,
        dto.getName()
    );
    return ChatMapper.INSTANCE.toDto(chat);
  }

  @PutMapping("/chats/group/{id}")
  public void leaveGroup(@PathVariable("id") Long id) {
    User user = userService.find(PrincipalHolder.getUserId());
    chatService.leaveGroup(id, user);
  }

  @DeleteMapping("/chats/group/{id}")
  public void deleteGroup(@PathVariable("id") Long id) {
    User owner = userService.find(PrincipalHolder.getUserId());
    chatService.deleteGroup(id, owner);
  }

  @PutMapping("/chats/group/{id}/members")
  public ChatDto updateGroupMembers(@PathVariable Long id,
                                    @Valid @RequestBody GroupMembersDto dto) {
    User member = userService.find(PrincipalHolder.getUserId());
    Set<User> members = findUsersByIds(dto.getMembers());
    GroupChat chat = chatService.updateGroupMembers(id, member, members);
    return ChatMapper.INSTANCE.toDto(chat);
  }

  @PutMapping("/chats/group/{id}/members/{newOwnerId}")
  public GroupChatDto changeOwner(@PathVariable Long id,
                                  @PathVariable Long newOwnerId) {
    User owner = userService.find(PrincipalHolder.getUserId());
    User newOwner = userService.find(newOwnerId);
    GroupChat chat = chatService.changeOwner(id, owner, newOwner);
    return ChatMapper.INSTANCE.toDto(chat);
  }

  private Set<User> findUsersByIds(List<Long> ids) {
    return ids.stream()
        .map(userService::find)
        .collect(Collectors.toSet());
  }

}
