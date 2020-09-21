package com.social.backend.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
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
import com.social.backend.repository.ChatRepositoryImpl;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import({ChatServiceImpl.class, ChatRepositoryImpl.class})
public class ChatServiceTest {

  @Autowired
  private ChatService chatService;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void createPrivate_whenEntityAlreadyExists_expectException() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target"));
    entityManager.persist(new PrivateChat()
        .setMembers(Sets
            .newHashSet(user, target)));

    Assertions
        .assertThatThrownBy(() -> chatService.createPrivate(user, target))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.alreadyExist"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsNotPublicNorFriend_expectException() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.INTERNAL));

    Assertions
        .assertThatThrownBy(() -> chatService.createPrivate(user, target))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.createNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsFriend() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.INTERNAL));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    Assertions
        .assertThat(chatService.createPrivate(user, target))
        .usingRecursiveComparison()
        .ignoringFields("members.friends")
        .isEqualTo(new PrivateChat()
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("user@mail.com")
                        .setUsername("user"),
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("target@mail.com")
                        .setUsername("target")
                        .setPublicity(Publicity.INTERNAL)
                )
            ));
  }

  @Test
  public void createPrivate_whenTargetIsPublic() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC));

    Assertions
        .assertThat(chatService.createPrivate(user, target))
        .usingRecursiveComparison()
        .ignoringFields("members.friends")
        .isEqualTo(new PrivateChat()
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("user@mail.com")
                        .setUsername("user"),
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("target@mail.com")
                        .setUsername("target")
                        .setPublicity(Publicity.PUBLIC)
                )
            ));
  }

  @Test
  public void deletePrivate_whenNoEntityWithIdAndMember_expectException() {
    User user = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> chatService.deletePrivate(1L, user))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.private.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void deletePrivate() {
    User first = entityManager.persist(TestEntity
        .user()
        .setEmail("first@mail.com")
        .setUsername("first"));
    User second = entityManager.persist(TestEntity
        .user()
        .setEmail("second@mail.com")
        .setUsername("second"));
    entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(first, second)));

    chatService.deletePrivate(1L, first);

    Assertions
        .assertThat(entityManager.find(Chat.class, 1L))
        .isNull();
  }

  @Test
  public void createGroup_whenAnyMemberIsNotPublicNorFriend_expectException() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPublicity(Publicity.INTERNAL));

    Assertions
        .assertThatThrownBy(() -> chatService.createGroup(owner, "name", ImmutableSet.of(member)))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.addNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createGroup_whenMembersAreFriends() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPublicity(Publicity.INTERNAL));
    owner.setFriends(Sets.newHashSet(member));
    member.setFriends(Sets.newHashSet(owner));

    Assertions
        .assertThat(chatService.createGroup(owner, "name", ImmutableSet.of(member)))
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("owner.friends", "members.friends")
        .isEqualTo(new GroupChat()
            .setName("name")
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"),
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("member@mail.com")
                        .setUsername("member")
                        .setPublicity(Publicity.INTERNAL)
                )
            ));
  }

  @Test
  public void createGroup_whenMembersArePublic() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPublicity(Publicity.PUBLIC));

    Assertions
        .assertThat(chatService.createGroup(owner, "name", ImmutableSet.of(member)))
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("owner.friends", "members.friends")
        .isEqualTo(new GroupChat()
            .setName("name")
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"),
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("member@mail.com")
                        .setUsername("member")
                        .setPublicity(Publicity.PUBLIC)
                )
            ));
  }

  @Test
  public void updateGroup_whenNoEntityWithIdAndMember_expectException() {
    User user = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> chatService.updateGroup(0L, user, "new name"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroup() {
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member"));
    entityManager.persist(TestEntity.groupChat()
        .setOwner(member))
        .setMembers(Sets
            .newHashSet(member));

    Assertions
        .assertThat(chatService.updateGroup(1L, member, "new name"))
        .usingComparator(TestComparator
            .chatComparator())
        .isEqualTo(new GroupChat()
            .setName("new name")
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("member@mail.com")
                .setUsername("member"))
            .setId(1L));
  }

  @Test
  public void updateGroupMembers_whenNoEntityWithIdAndOwner_expectException() {
    User owner = entityManager.persist(TestEntity
        .user());

    Assertions
        .assertThatThrownBy(() -> chatService.updateGroupMembers(0L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroupMembers_whenNoOwnerInMemberList_expectException() {
    User owner = entityManager.persist(TestEntity.user());
    entityManager.persist(TestEntity.groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> chatService.updateGroupMembers(1L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.removeOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void updateGroupMembers_whenAnyNewMemberIsNotPublicNorFriend_expectException() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User newMember = entityManager.persist(TestEntity
        .user()
        .setEmail("newMember@mail.com")
        .setUsername("newMember")
        .setPublicity(Publicity.INTERNAL));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() ->
            chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.addNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void updateGroupMembers_whenMemberIsFriend_expectAddNewMember() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User newMember = entityManager.persist(TestEntity
        .user()
        .setEmail("newMember@mail.com")
        .setUsername("newMember")
        .setPublicity(Publicity.INTERNAL));
    owner.setFriends(Sets.newHashSet(newMember));
    newMember.setFriends(Sets.newHashSet(owner));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringCollectionOrder()
        .ignoringFields("owner.friends", "members.friends")
        .isEqualTo(TestEntity
            .groupChat()
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"),
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("newMember@mail.com")
                        .setUsername("newMember")
                        .setPublicity(Publicity.INTERNAL)
                )
            ));
  }

  @Test
  public void updateGroupMembers_whenMemberIsPublic_expectAddNewMember() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User newMember = entityManager.persist(TestEntity
        .user()
        .setEmail("newMember@mail.com")
        .setUsername("newMember")
        .setPublicity(Publicity.PUBLIC));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringCollectionOrder()
        .ignoringFields("owner.friends", "members.friends")
        .isEqualTo(TestEntity.groupChat()
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"),
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("newMember@mail.com")
                        .setUsername("newMember")
                        .setPublicity(Publicity.PUBLIC)
                )
            ));
  }

  @Test
  public void updateGroupMembers_whenAbsent_expectRemoveMember() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    Assertions
        .assertThat(chatService.updateGroupMembers(1L, owner, ImmutableSet.of(owner)))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringCollectionOrder()
        .isEqualTo(TestEntity
            .groupChat()
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(TestEntity
                    .user()
                    .setId(1L)
                    .setEmail("owner@mail.com")
                    .setUsername("owner"))));
  }

  @Test
  public void changeOwner_whenNoEntityWithIdAndOwner_expectException() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User newOwner = entityManager.persist(TestEntity
        .user()
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner"));

    Assertions
        .assertThatThrownBy(() -> chatService.changeOwner(0L, owner, newOwner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void changeOwner_whenNewOwnerIsNotMember_expectException() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User newOwner = entityManager.persist(TestEntity
        .user()
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner));

    Assertions
        .assertThatThrownBy(() -> chatService.changeOwner(1L, owner, newOwner))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.setOwnerNotMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
  }

  @Test
  public void changeOwner() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User newOwner = entityManager.persist(TestEntity
        .user()
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, newOwner)));

    Assertions
        .assertThat(chatService.changeOwner(1L, owner, newOwner))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringCollectionOrder()
        .isEqualTo(TestEntity
            .groupChat()
            .setOwner(TestEntity
                .user()
                .setId(2L)
                .setEmail("newOwner@mail.com")
                .setUsername("newOwner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(
                    TestEntity
                        .user()
                        .setId(2L)
                        .setEmail("newOwner@mail.com")
                        .setUsername("newOwner"),
                    TestEntity
                        .user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner")
                )
            ));
  }

  @Test
  public void leaveGroup_whenNoEntityWithIdAndMember_expectException() {
    User user = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> chatService.leaveGroup(0L, user))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void leaveGroup_whenLeavingMemberIsOwner_expectException() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> chatService.leaveGroup(1L, owner))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.leaveOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void leaveGroup() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    chatService.leaveGroup(1L, member);

    Assertions
        .assertThat(entityManager.find(Chat.class, 1L))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(TestEntity
            .groupChat()
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L)
            .setMembers(Sets
                .newHashSet(TestEntity
                    .user()
                    .setId(1L)
                    .setEmail("owner@mail.com")
                    .setUsername("owner"))
            ));
  }

  @Test
  public void deleteGroup_whenNoEntityWithIdAndOwner_expectException() {
    User owner = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> chatService.deleteGroup(0L, owner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void deleteGroup() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    chatService.deleteGroup(1L, owner);

    Assertions
        .assertThat(entityManager.find(Chat.class, 1L))
        .isNull();
  }

  @Test
  public void getMembers_whenNoEntityWithIdAndMember_expectException() {
    User user = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> chatService.getMembers(0L, user, Pageable.unpaged()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void getMembers() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    Assertions
        .assertThat(chatService.getMembers(1L, owner, Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .userComparator(), User.class)
        .containsExactlyInAnyOrder(
            TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"),
            TestEntity
                .user()
                .setId(2L)
                .setEmail("member@mail.com")
                .setUsername("member")
        );
  }

  @Test
  public void find_byIdAndMember_whenNoEntityWithIdAndMember_expectException() {
    User user = entityManager.persist(TestEntity
        .user());

    Assertions
        .assertThatThrownBy(() -> chatService.find(0L, user))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void find_byIdAndMember() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(chatService.find(1L, owner))
        .usingComparator(TestComparator
            .chatComparator())
        .isEqualTo(TestEntity
            .groupChat()
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L));
  }

  @Test
  public void findAll_byOwner() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner"));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(chatService.findAll(owner, Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .chatComparator(), Chat.class)
        .containsExactly(TestEntity
            .groupChat()
            .setOwner(TestEntity
                .user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"))
            .setId(1L));
  }

}
