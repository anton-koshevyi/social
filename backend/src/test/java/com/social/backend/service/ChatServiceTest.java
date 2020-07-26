package com.social.backend.service;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;

import static com.social.backend.TestComparator.chatComparator;
import static com.social.backend.TestComparator.userComparator;
import static com.social.backend.TestEntity.groupChat;
import static com.social.backend.TestEntity.privateChat;
import static com.social.backend.TestEntity.user;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(ChatServiceImpl.class)
public class ChatServiceTest {
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void createPrivate_exception_whenAlreadyExists() {
        User user = entityManager.persist(user()
                .setEmail("user@mail.com")
                .setUsername("user"));
        User target = entityManager.persist(user()
                .setEmail("target@mail.com")
                .setUsername("target"));
        entityManager.persist(privateChat()
                .setMembers(list(user, target)));
        
        assertThatThrownBy(() -> chatService.createPrivate(user, target))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.private.alreadyExist"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void createPrivate_exception_whenTargetIsNotPublicNorFriend() {
        User user = entityManager.persist(user()
                .setEmail("user@mail.com")
                .setUsername("user"));
        User target = entityManager.persist(user()
                .setEmail("target@mail.com")
                .setUsername("target")
                .setPublicity(Publicity.INTERNAL));
        
        assertThatThrownBy(() -> chatService.createPrivate(user, target))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes",
                        new Object[]{"illegalAction.chat.private.createWithNotFriend"})
                .hasFieldOrPropertyWithValue("getArguments",
                        new Object[]{2L});
    }
    
    @Test
    public void createPrivate_whenTargetIsFriend() {
        User user = entityManager.persist(user()
                .setEmail("user@mail.com")
                .setUsername("user")
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("target@mail.com")
                        .setUsername("target")
                        .setPublicity(Publicity.INTERNAL))));
        User target = entityManager.persist(user()
                .setEmail("target@mail.com")
                .setUsername("target")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("user@mail.com")
                        .setUsername("user"))));
        
        assertThat(chatService.createPrivate(user, target))
                .usingRecursiveComparison()
                .ignoringFields("members.friends")
                .isEqualTo(privateChat()
                        .setId(1L)
                        .setMembers(list(
                                user()
                                        .setId(1L)
                                        .setEmail("user@mail.com")
                                        .setUsername("user"),
                                user()
                                        .setId(2L)
                                        .setEmail("target@mail.com")
                                        .setUsername("target")
                                        .setPublicity(Publicity.INTERNAL)
                        )));
    }
    
    @Test
    public void createPrivate_whenTargetIsPublic() {
        User user = entityManager.persist(user()
                .setEmail("user@mail.com")
                .setUsername("user"));
        User target = entityManager.persist(user()
                .setEmail("target@mail.com")
                .setUsername("target")
                .setPublicity(Publicity.PUBLIC));
        
        assertThat(chatService.createPrivate(user, target))
                .usingRecursiveComparison()
                .ignoringFields("members.friends")
                .isEqualTo(privateChat()
                        .setId(1L)
                        .setMembers(list(
                                user()
                                        .setId(1L)
                                        .setEmail("user@mail.com")
                                        .setUsername("user"),
                                user()
                                        .setId(2L)
                                        .setEmail("target@mail.com")
                                        .setUsername("target")
                                        .setPublicity(Publicity.PUBLIC)
                        )));
    }
    
    @Test
    public void createGroup_exception_whenAnyMemberIsNotPublicNorFriend() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member")
                .setPublicity(Publicity.INTERNAL));
        
        assertThatThrownBy(() -> chatService.createGroup(owner, "name", list(member)))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes",
                        new Object[]{"illegalAction.chat.group.createWithNotFriend"})
                .hasFieldOrPropertyWithValue("getArguments",
                        new Object[]{2L});
    }
    
    @Test
    public void createGroup_whenMembersAreFriends() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner")
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("member@mail.com")
                        .setUsername("member")
                        .setPublicity(Publicity.INTERNAL))));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"))));
        
        assertThat(chatService.createGroup(owner, "name", list(member)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(list(
                                user()
                                        .setId(1L)
                                        .setEmail("owner@mail.com")
                                        .setUsername("owner"),
                                user()
                                        .setId(2L)
                                        .setEmail("member@mail.com")
                                        .setUsername("member")
                                        .setPublicity(Publicity.INTERNAL)
                        )));
    }
    
    @Test
    public void createGroup_whenMembersArePublic() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member")
                .setPublicity(Publicity.PUBLIC));
        
        assertThat(chatService.createGroup(owner, "name", list(member)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(list(
                                user()
                                        .setId(1L)
                                        .setEmail("owner@mail.com")
                                        .setUsername("owner"),
                                user()
                                        .setId(2L)
                                        .setEmail("member@mail.com")
                                        .setUsername("member")
                                        .setPublicity(Publicity.PUBLIC)
                        )));
    }
    
    @Test
    public void updateGroup_exception_whenNoGroupWithIdAndUser() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.updateGroup(0L, user, "new name", Collections.emptyList()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndUser"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void updateGroup_exception_whenNewMemberAlreadyExists() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.PUBLIC));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, newMember)));
        
        assertThatThrownBy(() -> chatService.updateGroup(1L, owner, "name", list(newMember)))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.addExistentMember"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void updateGroup_exception_whenAnyNewMemberIsNotPublicNorFriend() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.INTERNAL));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        assertThatThrownBy(() -> chatService.updateGroup(1L, owner, "name", list(newMember)))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.addNotFriend"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void updateGroup_whenNewMembersAreFriends() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner")
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("newMember@mail.com")
                        .setUsername("newMember")
                        .setPublicity(Publicity.INTERNAL))));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"))));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        assertThat(chatService.updateGroup(1L, owner, "name", list(newMember)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(list(
                                user()
                                        .setId(1L)
                                        .setEmail("owner@mail.com")
                                        .setUsername("owner"),
                                user()
                                        .setId(2L)
                                        .setEmail("newMember@mail.com")
                                        .setUsername("newMember")
                                        .setPublicity(Publicity.INTERNAL)
                        )));
    }
    
    @Test
    public void updateGroup_whenNewMembersArePublic() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.PUBLIC));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        assertThat(chatService.updateGroup(1L, owner, "name", list(newMember)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(list(
                                user()
                                        .setId(1L)
                                        .setEmail("owner@mail.com")
                                        .setUsername("owner"),
                                user()
                                        .setId(2L)
                                        .setEmail("newMember@mail.com")
                                        .setUsername("newMember")
                                        .setPublicity(Publicity.PUBLIC)
                        )));
    }
    
    @Test
    public void deletePrivate_exception_whenNoEntityWithIdAndUser() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.deletePrivate(1L, user))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndUser"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
    }
    
    @Test
    public void deletePrivate() {
        User first = entityManager.persist(user()
                .setEmail("first@mail.com")
                .setUsername("first"));
        User second = entityManager.persist(user()
                .setEmail("second@mail.com")
                .setUsername("second"));
        entityManager.persist(privateChat()
                .setMembers(list(first, second)));
        
        chatService.deletePrivate(1L, first);
        
        assertThat(entityManager.find(Chat.class, 1L))
                .isNull();
    }
    
    @Test
    public void leaveGroup_exception_whenNoEntityWithIdAndUser() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.leaveGroup(0L, user))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndUser"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void leaveGroup_exception_whenLeavingUserIsOwner() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        assertThatThrownBy(() -> chatService.leaveGroup(1L, owner))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.removeOwner"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
    }
    
    @Test
    public void leaveGroup() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, member)));
        
        chatService.leaveGroup(1L, member);
        
        assertThat(entityManager.find(Chat.class, 1L))
                .usingRecursiveComparison()
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(list(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))));
    }
    
    @Test
    public void removeGroupMembers_exception_whenNoEntityWithIdAndOwnerId() {
        entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        
        assertThatThrownBy(() -> chatService.removeGroupMembers(0L, 1L, Collections.emptyList()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwnerId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void removeGroupMembers_exception_whenRemovingUserIsOwner() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        assertThatThrownBy(() -> chatService.removeGroupMembers(1L, 1L, list(owner)))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.removeOwner"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
    }
    
    @Test
    public void removeGroupMembers() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, member)));
        
        assertThat(chatService.removeGroupMembers(1L, 1L, list(member)))
                .usingRecursiveComparison()
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(list(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))));
    }
    
    @Test
    public void deleteGroup_exception_whenNoEntityWithIdAndOwnerId() {
        entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        
        assertThatThrownBy(() -> chatService.deleteGroup(0L, 1L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwnerId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void deleteGroup() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        chatService.deleteGroup(1L, 1L);
        
        assertThat(entityManager.find(Chat.class, 1L))
                .isNull();
    }
    
    @Test
    public void setOwner_exception_whenNoEntityWithIdAndOwnerId() {
        entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newOwner = entityManager.persist(user()
                .setEmail("newOwner@mail.com")
                .setUsername("newOwner"));
        
        assertThatThrownBy(() -> chatService.setOwner(0L, 1L, newOwner))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwnerId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void setOwner_exception_whenNewOwnerIsNotMember() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newOwner = entityManager.persist(user()
                .setEmail("newOwner@mail.com")
                .setUsername("newOwner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner)));
        
        assertThatThrownBy(() -> chatService.setOwner(1L, 1L, newOwner))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.setOwnerNotMember"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void setOwner() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newOwner = entityManager.persist(user()
                .setEmail("newOwner@mail.com")
                .setUsername("newOwner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, newOwner)));
        
        assertThat(chatService.setOwner(1L, 1L, newOwner))
                .usingComparator(chatComparator())
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(2L)
                                .setEmail("newOwner@mail.com")
                                .setUsername("newOwner"))
                        .setId(1L));
    }
    
    @Test
    public void getMembers_exception_onNullPageable() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.getMembers(0L, user, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void getMembers_exception_whenNoEntityWithIdAndUser() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.getMembers(0L, user, Pageable.unpaged()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndUser"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void getMembers() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, member)));
        
        assertThat(chatService.getMembers(1L, owner, Pageable.unpaged()))
                .usingComparatorForType(userComparator(), User.class)
                .containsExactlyInAnyOrder(
                        user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"),
                        user()
                                .setId(2L)
                                .setEmail("member@mail.com")
                                .setUsername("member")
                );
    }
    
    @Test
    public void findByIdAndUser_exception_whenNoEntityWithIdAndUser() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.findByIdAndUser(0L, user))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndUser"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void findByIdAndUser() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, member)));
        
        assertThat(chatService.findByIdAndUser(1L, owner))
                .usingComparator(chatComparator())
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L));
    }
    
    @Test
    public void findAllByUser_exception_onNullPageable() {
        assertThatThrownBy(() -> chatService.findAllByUser(user(), null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAllByUser() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(list(owner, member)));
        
        assertThat(chatService.findAllByUser(owner, Pageable.unpaged()))
                .usingComparatorForType(chatComparator(), Chat.class)
                .containsExactly(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L));
    }
}
