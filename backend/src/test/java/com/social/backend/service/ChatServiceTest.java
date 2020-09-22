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
import com.social.backend.test.TestEntity;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.stub.repository.ChatRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class ChatServiceTest {

  private IdentificationContext<Chat> identification;
  private ChatRepositoryStub repository;
  private ChatService service;

  @BeforeEach
  public void setUp() {
    identification = new IdentificationContext<>();
    repository = new ChatRepositoryStub(identification);
    service = new ChatServiceImpl(repository);
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(new PrivateChat()
        .setMembers(Sets
            .newHashSet(user, target)));

    Assertions
        .assertThatThrownBy(() -> service.createPrivate(user, target))
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
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThatThrownBy(() -> service.createPrivate(user, target))
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
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createPrivate(user, target))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
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
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createPrivate(user, target))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
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
        .assertThatThrownBy(() -> service.deletePrivate(1L, user))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(first, second)));

    service.deletePrivate(1L, first);

    Assertions
        .assertThat(repository.find(1L))
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
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThatThrownBy(() -> service.createGroup(owner, "name", ImmutableSet.of(member)))
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
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createGroup(owner, "name", ImmutableSet.of(member)))
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
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createGroup(owner, "name", ImmutableSet.of(member)))
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
        .assertThatThrownBy(() -> service.updateGroup(0L, user, "new name"))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity.groupChat()
        .setOwner(member))
        .setMembers(Sets
            .newHashSet(member));

    Assertions
        .assertThat(service.updateGroup(1L, member, "new name"))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
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
        .assertThatThrownBy(() -> service.updateGroupMembers(0L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroupMembers_whenNoOwnerInMemberList_expectException() {
    User owner = TestEntity
        .user()
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity.groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> service.updateGroupMembers(1L, owner, ImmutableSet.of()))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() ->
            service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner)))
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
        .assertThatThrownBy(() -> service.changeOwner(0L, owner, newOwner))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner));

    Assertions
        .assertThatThrownBy(() -> service.changeOwner(1L, owner, newOwner))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, newOwner)));

    Assertions
        .assertThat(service.changeOwner(1L, owner, newOwner))
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
        .assertThatThrownBy(() -> service.leaveGroup(0L, user))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> service.leaveGroup(1L, owner))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    service.leaveGroup(1L, member);

    Assertions
        .assertThat(repository.find(1L))
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
        .assertThatThrownBy(() -> service.deleteGroup(0L, owner))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    service.deleteGroup(1L, owner);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
  }

  @Test
  public void getMembers_whenNoEntityWithIdAndMember_expectException() {
    User user = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.getMembers(0L, user, Pageable.unpaged()))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));

    Assertions
        .assertThat(service.getMembers(1L, owner, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(User.class), User.class)
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
        .assertThatThrownBy(() -> service.find(0L, user))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(service.find(1L, owner))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));

    Assertions
        .assertThat(service.findAll(owner, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Chat.class), Chat.class)
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
