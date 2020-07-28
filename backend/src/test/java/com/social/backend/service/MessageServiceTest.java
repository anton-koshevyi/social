package com.social.backend.service;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.social.backend.TestComparator.messageComparator;
import static com.social.backend.TestComparator.notNullFirst;
import static com.social.backend.TestEntity.message;
import static com.social.backend.TestEntity.privateChat;
import static com.social.backend.TestEntity.user;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(MessageServiceImpl.class)
public class MessageServiceTest {
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void create() {
        User author = entityManager.persist(user()
                .setEmail("author@mail.com")
                .setUsername("author"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(author)));
    
        messageService.create(chat, author, "body");
    
        assertThat(entityManager.find(Message.class, 1L))
                .usingComparator(messageComparator())
                .isEqualTo(new Message()
                        .setChat(privateChat()
                                .setId(1L))
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(user()
                                .setId(1L)
                                .setEmail("author@mail.com")
                                .setUsername("author")));
    }
    
    @Test
    public void update_exception_whenNoEntityWithIdAndAuthor() {
        User author = entityManager.persist(user());
        
        assertThatThrownBy(() -> messageService.update(0L, author, "body"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.message.byIdAndAuthor"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void update() {
        User author = entityManager.persist(user()
                .setEmail("author@mail.com")
                .setUsername("author"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(author)));
        entityManager.persist(new Message()
                .setChat(chat)
                .setBody("message body")
                .setAuthor(author));
    
        messageService.update(1L, author, "new body");
    
        assertThat(entityManager.find(Message.class, 1L))
                .usingComparator(messageComparator())
                .usingComparatorForFields(notNullFirst(), "updated")
                .isEqualTo(new Message()
                        .setChat(privateChat()
                                .setId(1L))
                        .setId(1L)
                        .setBody("new body")
                        .setAuthor(user()
                                .setId(1L)
                                .setEmail("author@mail.com")
                                .setUsername("author")));
    }
    
    @Test
    public void delete_exception_whenNoEntityWithIdAndAuthor() {
        User author = entityManager.persist(user());
        
        assertThatThrownBy(() -> messageService.delete(0L, author))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.message.byIdAndAuthor"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void delete() {
        User author = entityManager.persist(user()
                .setEmail("author@mail.com")
                .setUsername("author"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(author)));
        entityManager.persist(message()
                .setChat(chat)
                .setAuthor(author));
    
        messageService.delete(1L, author);
    
        assertThat(entityManager.find(Message.class, 1L))
                .isNull();
    }
    
    @Test
    public void findAllByChat() {
        User author = entityManager.persist(user()
                .setEmail("author@mail.com")
                .setUsername("author"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(author)));
        entityManager.persist(message()
                .setChat(chat)
                .setAuthor(author));
        
        assertThat(messageService.findAllByChat(chat, Pageable.unpaged()))
                .usingComparatorForType(messageComparator(), Message.class)
                .containsExactly((Message) message()
                        .setChat(privateChat()
                                .setId(1L))
                        .setId(1L)
                        .setAuthor(user()
                                .setId(1L)
                                .setEmail("author@mail.com")
                                .setUsername("author")));
    }
}
