package com.social.backend.service;

import java.util.Collections;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepository;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.chat.GroupChatType;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.user.UserType;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

  private @Mock ChatRepository repository;
  private ChatService service;

  @BeforeEach
  public void setUp() {
    service = new ChatServiceImpl(repository);
  }

  @Test
  public void createPrivate_whenEntityAlreadyExists_expectException() {
    User johnSmith = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Mockito
        .when(repository.existsPrivateByMembers(johnSmith, fredBloggs))
        .thenReturn(true);

    Assertions
        .assertThatThrownBy(() -> service.createPrivate(johnSmith, fredBloggs))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.alreadyExist"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsNotPublicNorFriend_expectException() {
    User johnSmith = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL);

    Assertions
        .assertThatThrownBy(() -> service.createPrivate(johnSmith, fredBloggs))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.private.createNotFriend"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void createPrivate_whenTargetIsFriend() {
    User johnSmith = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL)
        .setFriends(Sets.newHashSet(johnSmith));
    johnSmith.setFriends(Sets.newHashSet(fredBloggs));
    Mockito
        .when(repository.save(Mockito.any(PrivateChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.createPrivate(johnSmith, fredBloggs))
        .usingComparator(ComparatorFactory.getComparator(PrivateChat.class))
        .isEqualTo(new PrivateChat()
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.INTERNAL)
            ))
        );
  }

  @Test
  public void createPrivate_whenTargetIsPublic() {
    User johnSmith = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User fredBloggs = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    Mockito
        .when(repository.save(Mockito.any(PrivateChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.createPrivate(johnSmith, fredBloggs))
        .usingComparator(ComparatorFactory.getComparator(PrivateChat.class))
        .isEqualTo(new PrivateChat()
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC)
            ))
        );
  }

  @Test
  public void deletePrivate_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
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
    User johnSmith = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    PrivateChat entity = (PrivateChat) ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(johnSmith));
    Mockito
        .when(repository.findPrivateByIdAndMember(1L, johnSmith))
        .thenReturn(Optional.of(entity));

    service.deletePrivate(1L, johnSmith);

    Mockito
        .verify(repository)
        .delete(entity);
  }

  @Test
  public void createGroup_whenAnyMemberIsNotPublicNorFriend_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL);

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
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL)
        .setFriends(Sets.newHashSet(owner));
    owner.setFriends(Sets.newHashSet(member));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(new GroupChat()
            .setName("Classmates")
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.INTERNAL)
            ))
        );
  }

  @Test
  public void createGroup_whenMembersArePublic() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(new GroupChat()
            .setName("Classmates")
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC)
            ))
        );
  }

  @Test
  public void updateGroup_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.updateGroup(0L, member, "Classmates"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroup() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findGroupByIdAndMember(1L, member))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.SCIENTISTS)
            .setOwner(member)
            .setId(1L)
            .setMembers(Sets.newHashSet(member))));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroup(1L, member, "Classmates"))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.SCIENTISTS)
            .setName("Classmates")
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void updateGroupMembers_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.updateGroupMembers(0L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void updateGroupMembers_whenNoOwnerInMemberList_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner))));

    Assertions
        .assertThatThrownBy(() ->
            service.updateGroupMembers(1L, owner, Collections.emptySet()))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.removeOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void updateGroupMembers_whenAnyNewMemberIsNotPublicNorFriend_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner))));

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
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.INTERNAL)
        .setFriends(Sets.newHashSet(owner));
    owner.setFriends(Sets.newHashSet(newMember));
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner))));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.INTERNAL)
            ))
        );
  }

  @Test
  public void updateGroupMembers_whenMemberIsPublic_expectAddNewMember() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner))));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC)
            ))
        );
  }

  @Test
  public void updateGroupMembers_whenAbsent_expectRemoveMember() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner, member))));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void changeOwner_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newOwner = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);

    Assertions
        .assertThatThrownBy(() -> service.changeOwner(0L, owner, newOwner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void changeOwner_whenNewOwnerIsNotMember_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newOwner = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner))));

    Assertions
        .assertThatThrownBy(() -> service.changeOwner(1L, owner, newOwner))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.setOwnerNotMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
  }

  @Test
  public void changeOwner() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newOwner = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner, newOwner))));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.changeOwner(1L, owner, newOwner))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L),
                ModelFactory
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
            ))
        );
  }

  @Test
  public void leaveGroup_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.leaveGroup(0L, member))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void leaveGroup_whenLeavingMemberIsOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findGroupByIdAndMember(1L, owner))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner))));

    Assertions
        .assertThatThrownBy(() -> service.leaveGroup(1L, owner))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.chat.group.leaveOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void leaveGroup() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Mockito
        .when(repository.findGroupByIdAndMember(1L, member))
        .thenReturn(Optional.of((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner, member))));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    service.leaveGroup(1L, member);

    ArgumentCaptor<GroupChat> captor = ArgumentCaptor.forClass(GroupChat.class);
    Mockito
        .verify(repository)
        .save(captor.capture());
    Assertions
        .assertThat(captor.getValue())
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void deleteGroup_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.deleteGroup(0L, owner))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void deleteGroup() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Chat entity = ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setId(1L)
        .setMembers(Sets.newHashSet(owner));
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of((GroupChat) entity));

    service.deleteGroup(1L, owner);

    Mockito
        .verify(repository)
        .delete(entity);
  }

  @Test
  public void getMembers_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
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
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Mockito
        .when(repository.findByIdAndMember(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setMembers(Sets.newHashSet(owner, member))));

    Assertions
        .assertThat(service.getMembers(1L, owner, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(User.class), User.class)
        .containsExactlyInAnyOrder(
            ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L),
            ModelFactory
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L)
        );
  }

  @Test
  public void find_byIdAndMember_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
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
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findByIdAndMember(1L, member))
        .thenReturn(Optional.of(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(member)
            .setId(1L)
            .setMembers(Sets.newHashSet(member))));

    Assertions
        .assertThat(service.find(1L, member))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
        .isEqualTo(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

  @Test
  public void findAll_byMember() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findAllByMember(member, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(GroupChatType.CLASSMATES)
                .setOwner(member)
                .setId(1L)
                .setMembers(Sets.newHashSet(member)))
        ));

    Assertions
        .assertThat(service.findAll(member, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Chat.class), Chat.class)
        .containsExactly(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L))
            .setId(1L)
            .setMembers(Sets.newHashSet(
                ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
            ))
        );
  }

}
