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
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.model.ModelFactoryProducer;
import com.social.backend.test.model.chat.GroupChatType;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.user.UserType;
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
    User johnSmith = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(new PrivateChat()
        .setMembers(Sets.newHashSet(johnSmith, fredBloggs)));

    Assertions
        .assertThatThrownBy(() -> service.createPrivate(johnSmith, fredBloggs))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.alreadyExist"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsNotPublicNorFriend_expectException() {
    User johnSmith = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL);
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThatThrownBy(() -> service.createPrivate(johnSmith, fredBloggs))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.createNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsFriend() {
    User johnSmith = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL)
        .setFriends(Sets.newHashSet(johnSmith));
    johnSmith.setFriends(Sets.newHashSet(fredBloggs));
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createPrivate(johnSmith, fredBloggs))
        .usingComparator(ComparatorFactory.getComparator(PrivateChat.class))
        .isEqualTo(new PrivateChat()
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.INTERNAL)
            ))
        );
  }

  @Test
  public void createPrivate_whenTargetIsPublic() {
    User johnSmith = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createPrivate(johnSmith, fredBloggs))
        .usingComparator(ComparatorFactory.getComparator(PrivateChat.class))
        .isEqualTo(new PrivateChat()
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC)
            ))
        );
  }

  @Test
  public void deletePrivate_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.deletePrivate(1L, member))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.private.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void deletePrivate() {
    User johnSmith = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(PrivateChat.class)
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(johnSmith, fredBloggs)));

    service.deletePrivate(1L, johnSmith);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
  }

  @Test
  public void createGroup_whenAnyMemberIsNotPublicNorFriend_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL);
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThatThrownBy(() ->
            service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.addNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createGroup_whenMembersAreFriends() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL)
        .setFriends(Sets.newHashSet(owner));
    owner.setFriends(Sets.newHashSet(member));
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(new GroupChat()
            .setName("Classmates")
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.INTERNAL)
            ))
        );
  }

  @Test
  public void createGroup_whenMembersArePublic() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThat(service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(new GroupChat()
            .setName("Classmates")
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC)
            ))
        );
  }

  @Test
  public void updateGroup_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.updateGroup(0L, member, "new name"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroup() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.SCIENTISTS)
        .setOwner(member))
        .setMembers(Sets.newHashSet(member));

    Assertions
        .assertThat(service.updateGroup(1L, member, "Classmates"))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.SCIENTISTS)
            .setName("Classmates")
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void updateGroupMembers_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.updateGroupMembers(0L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroupMembers_whenNoOwnerInMemberList_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> service.updateGroupMembers(1L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.removeOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void updateGroupMembers_whenAnyNewMemberIsNotPublicNorFriend_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

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
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL)
        .setFriends(Sets.newHashSet(owner));
    owner.setFriends(Sets.newHashSet(newMember));
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.INTERNAL)
            ))
        );
  }

  @Test
  public void updateGroupMembers_whenMemberIsPublic_expectAddNewMember() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC)
            ))
        );
  }

  @Test
  public void updateGroupMembers_whenAbsent_expectRemoveMember() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner, member)));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void changeOwner_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newOwner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);

    Assertions
        .assertThatThrownBy(() -> service.changeOwner(0L, owner, newOwner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void changeOwner_whenNewOwnerIsNotMember_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newOwner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> service.changeOwner(1L, owner, newOwner))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.setOwnerNotMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
  }

  @Test
  public void changeOwner() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newOwner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner, newOwner)));

    Assertions
        .assertThat(service.changeOwner(1L, owner, newOwner))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
            ))
        );
  }

  @Test
  public void leaveGroup_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.leaveGroup(0L, member))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void leaveGroup_whenLeavingMemberIsOwner_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    Assertions
        .assertThatThrownBy(() -> service.leaveGroup(1L, owner))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.leaveOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void leaveGroup() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner, member)));

    service.leaveGroup(1L, member);

    Assertions
        .assertThat((GroupChat) repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void deleteGroup_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.deleteGroup(0L, owner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void deleteGroup() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    service.deleteGroup(1L, owner);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
  }

  @Test
  public void getMembers_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.getMembers(0L, member, Pageable.unpaged()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void getMembers() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner, member)));

    Assertions
        .assertThat(service.getMembers(1L, owner, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(User.class), User.class)
        .containsExactlyInAnyOrder(
            ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L),
            ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L)
        );
  }

  @Test
  public void find_byIdAndMember_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.find(0L, member))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void find_byIdAndMember() {
    User member = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));

    Assertions
        .assertThat(service.find(1L, member))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
        .isEqualTo(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void findAll_byOwner() {
    User owner = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(GroupChat.class)
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));

    Assertions
        .assertThat(service.findAll(owner, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Chat.class), Chat.class)
        .containsExactly(ModelFactoryProducer.getFactory(GroupChat.class)
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

}
