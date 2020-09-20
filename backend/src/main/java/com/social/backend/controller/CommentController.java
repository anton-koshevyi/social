package com.social.backend.controller;

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

import com.social.backend.common.PrincipalHolder;
import com.social.backend.dto.reply.CommentDto;
import com.social.backend.dto.reply.ContentDto;
import com.social.backend.dto.reply.ContentDto.CreateGroup;
import com.social.backend.dto.reply.ContentDto.UpdateGroup;
import com.social.backend.mapper.CommentMapper;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.service.CommentService;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;

@RestController
public class CommentController {

  private final CommentService commentService;
  private final PostService postService;
  private final UserService userService;

  @Autowired
  public CommentController(CommentService commentService,
                           PostService postService,
                           UserService userService) {
    this.commentService = commentService;
    this.postService = postService;
    this.userService = userService;
  }

  @GetMapping("/posts/{postId}/comments")
  public Page<CommentDto> getAll(@PathVariable Long postId,
                                 Pageable pageable) {
    Post post = postService.find(postId);
    Page<Comment> comments = commentService.findAll(post, pageable);
    return comments.map(CommentMapper.INSTANCE::toDto);
  }

  @PostMapping("/posts/{postId}/comments")
  public CommentDto create(@PathVariable Long postId,
                           @Validated(CreateGroup.class) @RequestBody ContentDto dto) {
    Post post = postService.find(postId);
    User author = userService.find(PrincipalHolder.getUserId());
    Comment comment = commentService.create(
        post,
        author,
        dto.getBody()
    );
    return CommentMapper.INSTANCE.toDto(comment);
  }

  @PatchMapping("/posts/{postId}/comments/{id}")
  public CommentDto update(@PathVariable Long id,
                           @Validated(UpdateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(PrincipalHolder.getUserId());
    Comment comment = commentService.update(
        id,
        author,
        dto.getBody()
    );
    return CommentMapper.INSTANCE.toDto(comment);
  }

  @DeleteMapping("/posts/{postId}/comments/{id}")
  public void delete(@PathVariable Long id) {
    User author = userService.find(PrincipalHolder.getUserId());
    commentService.delete(id, author);
  }

}
