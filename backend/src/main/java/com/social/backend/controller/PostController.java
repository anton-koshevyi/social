package com.social.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.post.ContentDto;
import com.social.backend.dto.post.ContentDto.CreateGroup;
import com.social.backend.dto.post.ContentDto.UpdateGroup;
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
  public Page<Post> getAll(Pageable pageable) {
    return postService.findAll(pageable);
  }

  @PostMapping("/posts")
  public Post create(@AuthenticationPrincipal(expression = "id") Long userId,
                     @Validated(CreateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(userId);
    String title = dto.getTitle();
    String body = dto.getBody();
    return postService.create(author, title, body);
  }

  @GetMapping("/posts/{id}")
  public Post get(@PathVariable Long id) {
    return postService.find(id);
  }

  @PatchMapping("/posts/{id}")
  public Post update(@PathVariable Long id,
                     @AuthenticationPrincipal(expression = "id") Long userId,
                     @Validated(UpdateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(userId);
    String title = dto.getTitle();
    String body = dto.getBody();
    return postService.update(id, author, title, body);
  }

  @DeleteMapping("/posts/{id}")
  public void delete(@PathVariable Long id,
                     @AuthenticationPrincipal(expression = "id") Long userId) {
    User author = userService.find(userId);
    postService.delete(id, author);
  }

}
