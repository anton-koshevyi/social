package com.social.service;

import java.util.Collections;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
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

import com.social.exception.IllegalActionException;
import com.social.exception.NotFoundException;
import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.PrivateChat;
import com.social.model.user.Publicity;
import com.social.model.user.User;
import com.social.repository.ChatRepository;
import com.social.test.comparator.ComparatorFactory;
import com.social.test.model.factory.ModelFactory;
import com.social.test.model.mutator.ChatMutators;
import com.social.test.model.mutator.UserMutators;
import com.social.test.model.type.GroupChatType;
import com.social.test.model.type.PrivateChatType;
import com.social.test.model.type.UserType;

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
        .createModel(UserType.JOHN_SMITH);
    User fredBloggs = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
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
        .createModel(UserType.JOHN_SMITH);
    User fredBloggs = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.INTERNAL));

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
        .createModel(UserType.JOHN_SMITH);
    User fredBloggs = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.INTERNAL),
            UserMutators.friends(johnSmith));
    UserMutators.friends(fredBloggs).accept(johnSmith);
    Mockito
        .when(repository.save(Mockito.any(PrivateChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          ChatMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.createPrivate(johnSmith, fredBloggs))
        .usingComparator(ComparatorFactory.getComparator(PrivateChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(PrivateChatType.RAW,
                ChatMutators.id(1L),
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModelMutating(UserType.FRED_BLOGGS,
                            UserMutators.publicity(Publicity.INTERNAL))
                )
            ));
  }

  @Test
  public void createPrivate_whenTargetIsPublic() {
    User johnSmith = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User fredBloggs = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.PUBLIC));
    Mockito
        .when(repository.save(Mockito.any(PrivateChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          ChatMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.createPrivate(johnSmith, fredBloggs))
        .usingComparator(ComparatorFactory.getComparator(PrivateChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(PrivateChatType.RAW,
                ChatMutators.id(1L),
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModelMutating(UserType.FRED_BLOGGS,
                            UserMutators.publicity(Publicity.PUBLIC))
                )
            ));
  }

  @Test
  public void deletePrivate_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);

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
        .createModel(UserType.JOHN_SMITH);
    PrivateChat entity = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(johnSmith));
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
        .createModel(UserType.JOHN_SMITH);
    User member = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.INTERNAL));

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
        .createModel(UserType.JOHN_SMITH);
    User member = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.INTERNAL),
            UserMutators.friends(owner));
    UserMutators.friends(member).accept(owner);
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          ChatMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.RAW,
                ChatMutators.id(1L),
                ChatMutators.name("Classmates"),
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModelMutating(UserType.FRED_BLOGGS,
                            UserMutators.publicity(Publicity.INTERNAL))
                ),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void createGroup_whenMembersArePublic() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User member = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.PUBLIC));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> {
          Chat entity = i.getArgument(0);
          ChatMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.createGroup(owner, "Classmates", ImmutableSet.of(member)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.RAW,
                ChatMutators.id(1L),
                ChatMutators.name("Classmates"),
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModelMutating(UserType.FRED_BLOGGS,
                            UserMutators.publicity(Publicity.PUBLIC))
                ),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void updateGroup_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() -> service.updateGroup(2L, member, "Classmates"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L, 1L});
  }

  @Test
  public void updateGroup() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(repository.findGroupByIdAndMember(2L, member))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.SCIENTISTS,
                ChatMutators.id(2L),
                ChatMutators.members(member),
                ChatMutators.owner(member))
        ));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroup(2L, member, "Classmates"))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.SCIENTISTS,
                ChatMutators.name("Classmates"),
                ChatMutators.members(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void updateGroupMembers_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() -> service.updateGroupMembers(1L, owner, ImmutableSet.of()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.chat.group.byIdAndOwner"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 1L});
  }

  @Test
  public void updateGroupMembers_whenNoOwnerInMemberList_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner),
                ChatMutators.owner(owner))
        ));

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
        .createModel(UserType.JOHN_SMITH);
    User newMember = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.INTERNAL));
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner),
                ChatMutators.owner(owner))
        ));

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
        .createModel(UserType.JOHN_SMITH);
    User newMember = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.INTERNAL),
            UserMutators.friends(owner));
    UserMutators.friends(newMember).accept(owner);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner),
                ChatMutators.owner(owner))
        ));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModelMutating(UserType.FRED_BLOGGS,
                            UserMutators.publicity(Publicity.INTERNAL))
                )
            ));
  }

  @Test
  public void updateGroupMembers_whenMemberIsPublic_expectAddNewMember() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User newMember = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.PUBLIC));
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner),
                ChatMutators.owner(owner))
        ));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner, newMember)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModelMutating(UserType.FRED_BLOGGS,
                            UserMutators.publicity(Publicity.PUBLIC))
                ),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void updateGroupMembers_whenAbsent_expectRemoveMember() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner, member),
                ChatMutators.owner(owner))
        ));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateGroupMembers(1L, owner, ImmutableSet.of(owner)))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void changeOwner_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User newOwner = ModelFactory
        .createModel(UserType.FRED_BLOGGS);

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
        .createModel(UserType.JOHN_SMITH);
    User newOwner = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner),
                ChatMutators.owner(owner))
        ));

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
        .createModel(UserType.JOHN_SMITH);
    User newOwner = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner, newOwner),
                ChatMutators.owner(owner))
        ));
    Mockito
        .when(repository.save(Mockito.any(GroupChat.class)))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.changeOwner(1L, owner, newOwner))
        .usingComparator(ComparatorFactory.getComparator(GroupChat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH),
                    ModelFactory
                        .createModel(UserType.FRED_BLOGGS)
                ),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.FRED_BLOGGS))
            ));
  }

  @Test
  public void leaveGroup_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);

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
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(repository.findGroupByIdAndMember(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner),
                ChatMutators.owner(owner))
        ));

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
        .createModel(UserType.JOHN_SMITH);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
    Mockito
        .when(repository.findGroupByIdAndMember(1L, member))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner, member),
                ChatMutators.owner(owner))
        ));
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
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void deleteGroup_whenNoEntityWithIdAndOwner_expectException() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);

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
        .createModel(UserType.JOHN_SMITH);
    GroupChat entity = ModelFactory
        .createModelMutating(GroupChatType.CLASSMATES,
            ChatMutators.members(owner),
            ChatMutators.owner(owner));
    Mockito
        .when(repository.findGroupByIdAndOwner(1L, owner))
        .thenReturn(Optional.of(entity));

    service.deleteGroup(1L, owner);

    Mockito
        .verify(repository)
        .delete(entity);
  }

  @Test
  public void getMembers_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() -> service.getMembers(0L, member, Pageable.unpaged()))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void getMembers() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
    Mockito
        .when(repository.findByIdAndMember(1L, owner))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(owner, member),
                ChatMutators.owner(owner))
        ));

    Assertions
        .assertThat(service.getMembers(1L, owner, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(User.class))
        .containsExactlyInAnyOrder(
            ModelFactory
                .createModel(UserType.JOHN_SMITH),
            ModelFactory
                .createModel(UserType.FRED_BLOGGS)
        );
  }

  @Test
  public void find_byIdAndMember_whenNoEntityWithIdAndMember_expectException() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() -> service.find(0L, member))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.chat.byIdAndMember"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void find_byIdAndMember() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(repository.findByIdAndMember(1L, member))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(member),
                ChatMutators.owner(member))
        ));

    Assertions
        .assertThat(service.find(1L, member))
        .usingComparator(ComparatorFactory.getComparator(Chat.class))
        .isEqualTo(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.members(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void findAll_byMember() {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(repository.findAllByMember(member, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(GroupChatType.CLASSMATES,
                    ChatMutators.members(member),
                    ChatMutators.owner(member)))
        ));

    Assertions
        .assertThat(service.findAll(member, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(Chat.class))
        .containsExactly(ModelFactory
            .createModelMutating(GroupChatType.CLASSMATES,
                ChatMutators.owner(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                ChatMutators.members(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

}
