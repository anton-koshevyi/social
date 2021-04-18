package com.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.common.PrincipalHolder;
import com.social.dto.post.ContentDto;
import com.social.dto.post.PostDto;
import com.social.mapper.PostMapper;
import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.service.PostService;
import com.social.service.UserService;

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
  public Page<PostDto> getAll(Pageable pageable) {
    Page<Post> posts = postService.findAll(pageable);
    return posts.map(PostMapper.INSTANCE::toDto);
  }

  @PostMapping("/posts")
  public PostDto create(@Validated(ContentDto.CreateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(PrincipalHolder.getUserId());
    Post post = postService.create(author, dto.getTitle(), dto.getBody());
    return PostMapper.INSTANCE.toDto(post);
  }

  @GetMapping("/posts/{id}")
  public PostDto get(@PathVariable Long id) {
    Post post = postService.find(id);
    return PostMapper.INSTANCE.toDto(post);
  }

  @PatchMapping("/posts/{id}")
  public PostDto update(@PathVariable Long id,
                        @Validated(ContentDto.UpdateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(PrincipalHolder.getUserId());
    Post post = postService.update(id, author, dto.getTitle(), dto.getBody());
    return PostMapper.INSTANCE.toDto(post);
  }

  @DeleteMapping("/posts/{id}")
  public void delete(@PathVariable Long id) {
    User author = userService.find(PrincipalHolder.getUserId());
    postService.delete(id, author);
  }

}
