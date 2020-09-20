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

import com.social.backend.dto.reply.ContentDto;
import com.social.backend.dto.reply.ContentDto.CreateGroup;
import com.social.backend.dto.reply.ContentDto.UpdateGroup;
import com.social.backend.dto.reply.MessageDto;
import com.social.backend.mapper.MessageMapper;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.MessageService;
import com.social.backend.service.UserService;

@RestController
public class MessageController {

  private final MessageService messageService;
  private final ChatService chatService;
  private final UserService userService;

  @Autowired
  public MessageController(MessageService messageService,
                           ChatService chatService,
                           UserService userService) {
    this.messageService = messageService;
    this.chatService = chatService;
    this.userService = userService;
  }

  @GetMapping("/chats/{chatId}/messages")
  public Page<MessageDto> getAll(@PathVariable Long chatId,
                                 @AuthenticationPrincipal(expression = "id") Long userId,
                                 Pageable pageable) {
    User member = userService.find(userId);
    Chat chat = chatService.find(chatId, member);
    Page<Message> messages = messageService.findAll(chat, pageable);
    return messages.map(MessageMapper.INSTANCE::toDto);
  }

  @PostMapping("/chats/{chatId}/messages")
  public MessageDto create(@PathVariable Long chatId,
                           @AuthenticationPrincipal(expression = "id") Long userId,
                           @Validated(CreateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(userId);
    Chat chat = chatService.find(chatId, author);
    Message message = messageService.create(
        chat,
        author,
        dto.getBody()
    );
    return MessageMapper.INSTANCE.toDto(message);
  }

  @PatchMapping("/chats/{chatId}/messages/{id}")
  public MessageDto update(@PathVariable Long id,
                           @AuthenticationPrincipal(expression = "id") Long userId,
                           @Validated(UpdateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(userId);
    Message message = messageService.update(
        id,
        author,
        dto.getBody()
    );
    return MessageMapper.INSTANCE.toDto(message);
  }

  @DeleteMapping("/chats/{chatId}/messages/{id}")
  public void delete(@PathVariable Long id,
                     @AuthenticationPrincipal(expression = "id") Long userId) {
    User author = userService.find(userId);
    messageService.delete(id, author);
  }

}
