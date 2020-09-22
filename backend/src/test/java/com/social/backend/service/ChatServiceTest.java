package com.social.backend.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;
import com.social.backend.test.stub.repository.ChatRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class ChatServiceTest {

  private IdentificationContext<Chat> chatIdentification;
  private ChatRepositoryStub chatRepository;
  private ChatService chatService;

  @BeforeEach
  public void setUp() {
    chatIdentification = new IdentificationContext<>();
    chatRepository = new ChatRepositoryStub(chatIdentification);

    chatService = new ChatServiceImpl(chatRepository);
  }

  @Test
  public void createPrivate_whenEntityAlreadyExists_expectException() {
    User user = TestEntity
        .user()
        .setId(1L)
        .setEmail("user@mail.com")
        .setUsername("user");
    User target = TestEntity
        .user()
        .setId(2L)
        .setEmail("target@mail.com")
        .setUsername("target");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(new PrivateChat()
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
    User user = TestEntity
        .user()
        .setId(1L)
        .setEmail("user@mail.com")
        .setUsername("user");
    User target = TestEntity
        .user()
        .setId(2L)
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.INTERNAL);
    chatIdentification.setStrategy(entity -> entity.setId(1L));

    Assertions
        .assertThatThrownBy(() -> chatService.createPrivate(user, target))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.createNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsFriend() {
    User user = TestEntity
        .user()
        .setId(1L)
        .setEmail("user@mail.com")
        .setUsername("user");
    User target = TestEntity
        .user()
        .setId(2L)
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.INTERNAL);
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));
    chatIdentification.setStrategy(entity -> entity.setId(1L));

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
    User user = TestEntity
        .user()
        .setId(1L)
        .setEmail("user@mail.com")
        .setUsername("user");
    User target = TestEntity
        .user()
        .setId(2L)
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC);
    chatIdentification.setStrategy(entity -> entity.setId(1L));

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
    User user = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.deletePrivate(1L, user))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.private.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void deletePrivate() {
    User first = TestEntity
        .user()
        .setId(1L)
        .setEmail("first@mail.com")
        .setUsername("first");
    User second = TestEntity
        .user()
        .setId(1L)
        .setEmail("second@mail.com")
        .setUsername("second");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(first, second)));

    chatService.deletePrivate(1L, first);

    Assertions
        .assertThat(chatRepository.find(1L))
        .isNull();
  }

  @Test
  public void createGroup_whenAnyMemberIsNotPublicNorFriend_expectException() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User member = TestEntity
        .user()
        .setId(2L)
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPublicity(Publicity.INTERNAL);
    chatIdentification.setStrategy(entity -> entity.setId(1L));

    Assertions
        .assertThatThrownBy(() -> chatService.createGroup(owner, "name", ImmutableSet.of(member)))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.addNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createGroup_whenMembersAreFriends() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User member = TestEntity
        .user()
        .setId(1L)
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPublicity(Publicity.INTERNAL);
    owner.setFriends(Sets.newHashSet(member));
    member.setFriends(Sets.newHashSet(owner));
    chatIdentification.setStrategy(entity -> entity.setId(1L));

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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User member = TestEntity
        .user()
        .setId(2L)
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPublicity(Publicity.PUBLIC);
    chatIdentification.setStrategy(entity -> entity.setId(1L));

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
    User user = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.updateGroup(0L, user, "new name"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroup() {
    User member = TestEntity
        .user()
        .setId(1L)
        .setEmail("member@mail.com")
        .setUsername("member");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity.groupChat()
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
    User owner = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.updateGroupMembers(0L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroupMembers_whenNoOwnerInMemberList_expectException() {
    User owner = TestEntity
        .user()
        .setId(1L);
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity.groupChat()
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User newMember = TestEntity
        .user()
        .setId(2L)
        .setEmail("newMember@mail.com")
        .setUsername("newMember")
        .setPublicity(Publicity.INTERNAL);
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User newMember = TestEntity
        .user()
        .setId(2L)
        .setEmail("newMember@mail.com")
        .setUsername("newMember")
        .setPublicity(Publicity.INTERNAL);
    owner.setFriends(Sets.newHashSet(newMember));
    newMember.setFriends(Sets.newHashSet(owner));
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User newMember = TestEntity
        .user()
        .setId(2L)
        .setEmail("newMember@mail.com")
        .setUsername("newMember")
        .setPublicity(Publicity.PUBLIC);
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User member = TestEntity
        .user()
        .setId(2L)
        .setEmail("member@mail.com")
        .setUsername("member");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User newOwner = TestEntity
        .user()
        .setId(2L)
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner");

    Assertions
        .assertThatThrownBy(() -> chatService.changeOwner(0L, owner, newOwner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void changeOwner_whenNewOwnerIsNotMember_expectException() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User newOwner = TestEntity
        .user()
        .setId(2L)
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User newOwner = TestEntity
        .user()
        .setId(2L)
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User user = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.leaveGroup(0L, user))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void leaveGroup_whenLeavingMemberIsOwner_expectException() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User member = TestEntity
        .user()
        .setId(2L)
        .setEmail("member@mail.com")
        .setUsername("member");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    chatService.leaveGroup(1L, member);

    Assertions
        .assertThat(chatRepository.find(1L))
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
    User owner = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.deleteGroup(0L, owner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void deleteGroup() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    chatService.deleteGroup(1L, owner);

    Assertions
        .assertThat(chatRepository.find(1L))
        .isNull();
  }

  @Test
  public void getMembers_whenNoEntityWithIdAndMember_expectException() {
    User user = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.getMembers(0L, user, Pageable.unpaged()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void getMembers() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    User member = TestEntity
        .user()
        .setId(2L)
        .setEmail("member@mail.com")
        .setUsername("member");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User user = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> chatService.find(0L, user))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void find_byIdAndMember() {
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
    User owner = TestEntity
        .user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    chatIdentification.setStrategy(entity -> entity.setId(1L));
    chatRepository.save(TestEntity
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
