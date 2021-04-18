package com.social.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.exception.IllegalActionException;
import com.social.exception.NotFoundException;
import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.PrivateChat;
import com.social.model.user.User;
import com.social.repository.ChatRepository;
import com.social.util.NullableUtils;

@Service
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;

  @Autowired
  public ChatServiceImpl(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  @Transactional
  @Override
  public PrivateChat createPrivate(User user, User target) {
    if (chatRepository.existsPrivateByMembers(user, target)) {
      throw new IllegalActionException(
          "illegalAction.chat.private.alreadyExist", target.getId());
    }

    if (!target.isPublic() && !target.hasFriendship(user)) {
      throw new IllegalActionException(
          "illegalAction.chat.private.createNotFriend", target.getId());
    }

    PrivateChat entity = new PrivateChat();
    entity.setMembers(Sets.newHashSet(user, target));
    return chatRepository.save(entity);
  }

  @Transactional
  @Override
  public void deletePrivate(Long id, User member) {
    Chat entity = findPrivateByIdAndMember(id, member);
    chatRepository.delete(entity);
  }

  @Transactional
  @Override
  public GroupChat createGroup(User creator, String name, Set<User> members) {
    for (User member : members) {
      if (!member.isPublic() && !member.hasFriendship(creator)) {
        throw new IllegalActionException(
            "illegalAction.chat.group.addNotFriend", member.getId());
      }
    }

    Set<User> finalMembers = new HashSet<>(members);
    finalMembers.add(creator);

    GroupChat entity = new GroupChat();
    entity.setName(name);
    entity.setOwner(creator);
    entity.setMembers(finalMembers);
    return chatRepository.save(entity);
  }

  @Transactional
  @Override
  public GroupChat updateGroup(Long id, User member, String name) {
    GroupChat entity = findGroupByIdAndMember(id, member);
    NullableUtils.set(entity::setName, name);
    return chatRepository.save(entity);
  }

  @Transactional
  @Override
  public GroupChat updateGroupMembers(Long id, User owner, Set<User> members) {
    GroupChat entity = findGroupByIdAndOwner(id, owner);

    if (!members.contains(owner)) {
      throw new IllegalActionException(
          "illegalAction.chat.group.removeOwner", id, owner.getId());
    }

    Set<User> finalMembers = new HashSet<>();

    for (User member : members) {
      if (entity.hasMember(member)) {
        finalMembers.add(member);
        continue;
      }

      if (!member.isPublic() && !member.hasFriendship(owner)) {
        throw new IllegalActionException(
            "illegalAction.chat.group.addNotFriend", member.getId());
      }

      finalMembers.add(member);
    }

    entity.setMembers(finalMembers);
    return chatRepository.save(entity);
  }

  @Transactional
  @Override
  public GroupChat changeOwner(Long id, User owner, User newOwner) {
    GroupChat entity = findGroupByIdAndOwner(id, owner);

    if (!entity.hasMember(newOwner)) {
      throw new IllegalActionException(
          "illegalAction.chat.group.setOwnerNotMember", id, newOwner.getId());
    }

    entity.setOwner(newOwner);
    return chatRepository.save(entity);
  }

  @Transactional
  @Override
  public void leaveGroup(Long id, User member) {
    GroupChat entity = findGroupByIdAndMember(id, member);

    if (entity.isOwner(member)) {
      throw new IllegalActionException(
          "illegalAction.chat.group.leaveOwner", id, member.getId());
    }

    Set<User> finalMembers = new HashSet<>(entity.getMembers());
    finalMembers.remove(member);
    entity.setMembers(finalMembers);
    chatRepository.save(entity);
  }

  @Transactional
  @Override
  public void deleteGroup(Long id, User owner) {
    GroupChat entity = findGroupByIdAndOwner(id, owner);
    chatRepository.delete(entity);
  }

  @Override
  public Page<User> getMembers(Long id, User member, Pageable pageable) {
    Set<User> members = this.find(id, member).getMembers();
    return new PageImpl<>(new ArrayList<>(members), pageable, members.size());
  }

  @Override
  public Chat find(Long id, User member) {
    return chatRepository.findByIdAndMember(id, member)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.byIdAndMember", id, member.getId()));
  }

  @Override
  public Page<Chat> findAll(User member, Pageable pageable) {
    return chatRepository.findAllByMember(member, pageable);
  }

  private PrivateChat findPrivateByIdAndMember(Long id, User member) {
    return chatRepository.findPrivateByIdAndMember(id, member)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.private.byIdAndMember", id, member.getId()));
  }

  private GroupChat findGroupByIdAndMember(Long id, User member) {
    return chatRepository.findGroupByIdAndMember(id, member)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.group.byIdAndMember", id, member.getId()));
  }

  private GroupChat findGroupByIdAndOwner(Long id, User owner) {
    return chatRepository.findGroupByIdAndOwner(id, owner)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.group.byIdAndOwner", id, owner.getId()));
  }

}
