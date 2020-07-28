package com.social.backend.service;

import com.google.common.collect.ImmutableSet;
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

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    public void createPrivate_exception_whenEntityAlreadyExists() {
        User user = entityManager.persist(user()
                .setEmail("user@mail.com")
                .setUsername("user"));
        User target = entityManager.persist(user()
                .setEmail("target@mail.com")
                .setUsername("target"));
        entityManager.persist(new PrivateChat()
                .setMembers(Sets.newHashSet(user, target)));
    
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
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.private.createNotFriend"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void createPrivate_whenTargetIsFriend() {
        User user = entityManager.persist(user()
                .setEmail("user@mail.com")
                .setUsername("user")
                .setFriends(Sets.newHashSet(user()
                        .setId(2L)
                        .setEmail("target@mail.com")
                        .setUsername("target")
                        .setPublicity(Publicity.INTERNAL))));
        User target = entityManager.persist(user()
                .setEmail("target@mail.com")
                .setUsername("target")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(Sets.newHashSet(user()
                        .setId(1L)
                        .setEmail("user@mail.com")
                        .setUsername("user"))));
    
        assertThat(chatService.createPrivate(user, target))
                .usingRecursiveComparison()
                .ignoringFields("members.friends")
                .isEqualTo(new PrivateChat()
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
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
                .isEqualTo(new PrivateChat()
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
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
    public void deletePrivate_exception_whenNoEntityWithIdAndMember() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.deletePrivate(1L, user))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.private.byIdAndMember"})
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
                .setMembers(Sets.newHashSet(first, second)));
        
        chatService.deletePrivate(1L, first);
        
        assertThat(entityManager.find(Chat.class, 1L))
                .isNull();
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
    
        assertThatThrownBy(() -> chatService.createGroup(owner, "name", ImmutableSet.of(member)))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.addNotFriend"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void createGroup_whenMembersAreFriends() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner")
                .setFriends(Sets.newHashSet(user()
                        .setId(2L)
                        .setEmail("member@mail.com")
                        .setUsername("member")
                        .setPublicity(Publicity.INTERNAL))));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(Sets.newHashSet(user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"))));
    
        assertThat(chatService.createGroup(owner, "name", ImmutableSet.of(member)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(new GroupChat()
                        .setName("name")
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
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
    
        assertThat(chatService.createGroup(owner, "name", ImmutableSet.of(member)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(new GroupChat()
                        .setName("name")
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
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
    public void updateGroup_exception_whenNoEntityWithIdAndMember() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.updateGroup(0L, user, "new name"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void updateGroup() {
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(member))
                .setMembers(Sets.newHashSet(member));
    
        assertThat(chatService.updateGroup(1L, member, "new name"))
                .usingComparator(chatComparator())
                .isEqualTo(new GroupChat()
                        .setName("new name")
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("member@mail.com")
                                .setUsername("member"))
                        .setId(1L));
    }
    
    @Test
    public void updateGroupMembers_exception_whenNoEntityWithIdAndOwner() {
        User owner = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.updateGroupMembers(0L, owner, ImmutableSet.of()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void updateGroupMembers_exception_whenMemberListDoesNotContainsOwner() {
        User owner = entityManager.persist(user());
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
    
        assertThatThrownBy(() -> chatService.updateGroupMembers(1L, owner, ImmutableSet.of()))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.removeOwner"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
    }
    
    @Test
    public void updateGroupMembers_exception_whenAnyNewMemberIsNotPublicNorFriend() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.INTERNAL));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
    
        assertThatThrownBy(() -> chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.addNotFriend"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void updateGroupMembers_addNewMember_whenMemberIsFriend() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner")
                .setFriends(Sets.newHashSet(user()
                        .setId(2L)
                        .setEmail("newMember@mail.com")
                        .setUsername("newMember")
                        .setPublicity(Publicity.INTERNAL))));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(Sets.newHashSet(user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"))));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
    
        assertThat(chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
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
    public void updateGroupMembers_addNewMember_whenMemberIsPublic() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newMember = entityManager.persist(user()
                .setEmail("newMember@mail.com")
                .setUsername("newMember")
                .setPublicity(Publicity.PUBLIC));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
    
        assertThat(chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringCollectionOrder()
                .ignoringFields("owner.friends", "members.friends")
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
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
    public void updateGroupMembers_removeMember_whenAbsent() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User member = entityManager.persist(user()
                .setEmail("member@mail.com")
                .setUsername("member"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner, member)));
        
        assertThat(chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner)))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringCollectionOrder()
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))));
    }
    
    @Test
    public void setOwner_exception_whenNoEntityWithIdAndOwner() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        User newOwner = entityManager.persist(user()
                .setEmail("newOwner@mail.com")
                .setUsername("newOwner"));
        
        assertThatThrownBy(() -> chatService.setOwner(0L, owner, newOwner))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
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
                .setOwner(owner));
    
        assertThatThrownBy(() -> chatService.setOwner(1L, owner, newOwner))
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
                .setMembers(Sets.newHashSet(owner, newOwner)));
    
        assertThat(chatService.setOwner(1L, owner, newOwner))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringCollectionOrder()
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(2L)
                                .setEmail("newOwner@mail.com")
                                .setUsername("newOwner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(
                                user()
                                        .setId(2L)
                                        .setEmail("newOwner@mail.com")
                                        .setUsername("newOwner"),
                                user()
                                        .setId(1L)
                                        .setEmail("owner@mail.com")
                                        .setUsername("owner")
                        )));
    }
    
    @Test
    public void leaveGroup_exception_whenNoEntityWithIdAndMember() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.leaveGroup(0L, user))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void leaveGroup_exception_whenLeavingMemberIsOwner() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
        
        assertThatThrownBy(() -> chatService.leaveGroup(1L, owner))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.chat.group.leaveOwner"})
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
                .setMembers(Sets.newHashSet(owner, member)));
        
        chatService.leaveGroup(1L, member);
        
        assertThat(entityManager.find(Chat.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L)
                        .setMembers(ImmutableSet.of(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))));
    }
    
    @Test
    public void deleteGroup_exception_whenNoEntityWithIdAndOwner() {
        User owner = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.deleteGroup(0L, owner))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void deleteGroup() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
    
        chatService.deleteGroup(1L, owner);
        
        assertThat(entityManager.find(Chat.class, 1L))
                .isNull();
    }
    
    @Test
    public void getMembers_exception_whenNoEntityWithIdAndMember() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.getMembers(0L, user, Pageable.unpaged()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
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
                .setMembers(Sets.newHashSet(owner, member)));
        
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
    public void findByIdAndMember_exception_whenNoEntityWithIdAndMember() {
        User user = entityManager.persist(user());
        
        assertThatThrownBy(() -> chatService.findByIdAndMember(0L, user))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void findByIdAndMember() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(ImmutableSet.of(owner)));
        
        assertThat(chatService.findByIdAndMember(1L, owner))
                .usingComparator(chatComparator())
                .isEqualTo(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L));
    }
    
    @Test
    public void findAllByMember() {
        User owner = entityManager.persist(user()
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        entityManager.persist(groupChat()
                .setOwner(owner)
                .setMembers(Sets.newHashSet(owner)));
        
        assertThat(chatService.findAllByMember(owner, Pageable.unpaged()))
                .usingComparatorForType(chatComparator(), Chat.class)
                .containsExactly(groupChat()
                        .setOwner(user()
                                .setId(1L)
                                .setEmail("owner@mail.com")
                                .setUsername("owner"))
                        .setId(1L));
    }
}
