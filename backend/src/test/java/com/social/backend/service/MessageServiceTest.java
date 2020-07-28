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
        User sender = entityManager.persist(user()
                .setEmail("sender@mail.com")
                .setUsername("sender"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(sender)));
        
        messageService.create(chat, sender, "body");
        
        assertThat(entityManager.find(Message.class, 1L))
                .usingComparator(messageComparator())
                .isEqualTo(new Message()
                        .setChat(privateChat()
                                .setId(1L))
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(user()
                                .setId(1L)
                                .setEmail("sender@mail.com")
                                .setUsername("sender")));
    }
    
    @Test
    public void update_exception_whenNoEntityWithIdAndAuthorId() {
        entityManager.persist(user());
        
        assertThatThrownBy(() -> messageService.update(0L, 1L, "body"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.message.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void update() {
        User sender = entityManager.persist(user()
                .setEmail("sender@mail.com")
                .setUsername("sender"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(sender)));
        entityManager.persist(new Message()
                .setChat(chat)
                .setBody("message body")
                .setAuthor(sender));
        
        messageService.update(1L, 1L, "new body");
        
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
                                .setEmail("sender@mail.com")
                                .setUsername("sender")));
    }
    
    @Test
    public void delete_exception_whenNoEntityWithIdAndAuthorId() {
        entityManager.persist(user());
        
        assertThatThrownBy(() -> messageService.delete(0L, 1L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.message.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void delete() {
        User sender = entityManager.persist(user()
                .setEmail("sender@mail.com")
                .setUsername("sender"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(sender)));
        entityManager.persist(message()
                .setChat(chat)
                .setAuthor(sender));
        
        messageService.delete(1L, 1L);
        
        assertThat(entityManager.find(Message.class, 1L))
                .isNull();
    }
    
    @Test
    public void findAllByChatId() {
        User sender = entityManager.persist(user()
                .setEmail("sender@mail.com")
                .setUsername("sender"));
        Chat chat = entityManager.persist(privateChat()
                .setMembers(Sets.newHashSet(sender)));
        entityManager.persist(message()
                .setChat(chat)
                .setAuthor(sender));
        
        assertThat(messageService.findAllByChatId(1L, Pageable.unpaged()))
                .usingComparatorForType(messageComparator(), Message.class)
                .containsExactly((Message) message()
                        .setChat(privateChat()
                                .setId(1L))
                        .setId(1L)
                        .setAuthor(user()
                                .setId(1L)
                                .setEmail("sender@mail.com")
                                .setUsername("sender")));
    }
}
