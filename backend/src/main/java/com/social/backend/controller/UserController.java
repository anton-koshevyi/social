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

import com.social.backend.dto.chat.PrivateChatDto;
import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.user.RoleDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.MapperProducer;
import com.social.backend.model.chat.PrivateChat;
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
                        PostService postService,
                        ChatService chatService) {
    this.userService = userService;
    this.postService = postService;
    this.chatService = chatService;
  }

  @GetMapping("/users")
  public Page<UserDto> getAll(Pageable pageable) {
    Page<User> users = userService.findAll(pageable);
    return users.map(user -> MapperProducer
        .getMapper(User.class)
        .map(user, UserDto.class));
  }

  @GetMapping("/users/{id}")
  public UserDto get(@PathVariable Long id) {
    User user = userService.find(id);
    return MapperProducer
        .getMapper(User.class)
        .map(user, UserDto.class);
  }

  @PatchMapping("/users/{id}/roles")
  public UserDto updateRole(@PathVariable Long id,
                            @Valid @RequestBody RoleDto dto) {
    User user = userService.updateRole(
        id,
        dto.getModer()
    );
    return MapperProducer
        .getMapper(User.class)
        .map(user, UserDto.class);
  }

  @GetMapping("/users/{id}/friends")
  public Page<UserDto> getFriends(@PathVariable Long id,
                                  Pageable pageable) {
    Page<User> users = userService.getFriends(id, pageable);
    return users.map(user -> MapperProducer
        .getMapper(User.class)
        .map(user, UserDto.class));
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
  public Page<PostDto> getPosts(@PathVariable Long id,
                                Pageable pageable) {
    User author = userService.find(id);
    Page<Post> posts = postService.findAll(author, pageable);
    return posts.map(post -> MapperProducer
        .getMapper(Post.class)
        .map(post, PostDto.class));
  }

  @PostMapping("/users/{id}/chats/private")
  public PrivateChatDto createPrivateChat(@AuthenticationPrincipal(expression = "id") Long userId,
                                          @PathVariable("id") Long targetId) {
    User user = userService.find(userId);
    User target = userService.find(targetId);
    PrivateChat chat = (PrivateChat) chatService.createPrivate(user, target);
    return MapperProducer
        .getMapper(PrivateChat.class)
        .map(chat, PrivateChatDto.class);
  }

}
