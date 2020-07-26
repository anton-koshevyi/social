package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.repository.MessageRepository;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    
    @Autowired
    public MessageServiceImpl(MessageRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Message create(Chat chat, User author, String body) {
        Message entity = new Message();
        entity.setBody(body);
        entity.setChat(chat);
        entity.setAuthor(author);
        return repository.save(entity);
    }
    
    @Override
    public Message update(Long id, Long authorId, String body) {
        Message entity = findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now());
        entity.setBody(body);
        return repository.save(entity);
    }
    
    @Override
    public void delete(Long id, Long authorId) {
        Message entity = findByIdAndAuthorId(id, authorId);
        repository.delete(entity);
    }
    
    @Override
    public Page<Message> findAllByChatId(Long chatId, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return repository.findAllByChatId(chatId, pageable);
    }
    
    private Message findByIdAndAuthorId(Long id, Long authorId) {
        return repository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new NotFoundException("notFound.message.byIdAndAuthorId", id, authorId));
    }
}
