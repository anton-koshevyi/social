package com.social.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.common.PrincipalHolder;
import com.social.dto.chat.PrivateChatDto;
import com.social.dto.post.PostDto;
import com.social.dto.user.RoleDto;
import com.social.dto.user.UserDto;
import com.social.mapper.ChatMapper;
import com.social.mapper.PostMapper;
import com.social.mapper.UserMapper;
import com.social.model.chat.PrivateChat;
import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.service.ChatService;
import com.social.service.PostService;
import com.social.service.UserService;

@RestController
public class UserController {

  private final UserService userService;
  private final PostService postService;
  private final ChatService chatService;

  @Autowired
  public UserController(UserService userService,
                        PostService postService,
                        ChatService chatService) {
    this.userService = userService;
    this.postService = postService;
    this.chatService = chatService;
  }

  @GetMapping("/users")
  public Page<UserDto> getAll(Pageable pageable) {
    Page<User> users = userService.findAll(pageable);
    return users.map(UserMapper.INSTANCE::toDto);
  }

  @GetMapping("/users/{id}")
  public UserDto get(@PathVariable Long id) {
    User user = userService.find(id);
    return UserMapper.INSTANCE.toDto(user);
  }

  @PatchMapping("/users/{id}/roles")
  public UserDto updateRole(@PathVariable Long id,
                            @Valid @RequestBody RoleDto dto) {
    User user = userService.updateRole(
        id,
        dto.getModer()
    );
    return UserMapper.INSTANCE.toDto(user);
  }

  @GetMapping("/users/{id}/friends")
  public Page<UserDto> getFriends(@PathVariable Long id,
                                  Pageable pageable) {
    Page<User> users = userService.getFriends(id, pageable);
    return users.map(UserMapper.INSTANCE::toDto);
  }

  @PostMapping("/users/{id}/friends")
  public void addFriend(@PathVariable("id") Long targetId) {
    userService.addFriend(PrincipalHolder.getUserId(), targetId);
  }

  @DeleteMapping("/users/{id}/friends")
  public void removeFriend(@PathVariable("id") Long targetId) {
    userService.removeFriend(PrincipalHolder.getUserId(), targetId);
  }

  @GetMapping("/users/{id}/posts")
  public Page<PostDto> getPosts(@PathVariable Long id,
                                Pageable pageable) {
    User author = userService.find(id);
    Page<Post> posts = postService.findAll(author, pageable);
    return posts.map(PostMapper.INSTANCE::toDto);
  }

  @PostMapping("/users/{id}/chats/private")
  public PrivateChatDto createPrivateChat(@PathVariable("id") Long targetId) {
    User user = userService.find(PrincipalHolder.getUserId());
    User target = userService.find(targetId);
    PrivateChat chat = chatService.createPrivate(user, target);
    return ChatMapper.INSTANCE.toDto(chat);
  }

}
