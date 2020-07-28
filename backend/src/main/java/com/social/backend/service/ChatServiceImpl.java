package com.social.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepositoryBase;
import com.social.backend.repository.ChatRepositoryGroup;
import com.social.backend.repository.ChatRepositoryPrivate;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {
    private final ChatRepositoryBase<Chat> baseRepository;
    private final ChatRepositoryPrivate privateRepository;
    private final ChatRepositoryGroup groupRepository;
    
    @Autowired
    public ChatServiceImpl(ChatRepositoryBase<Chat> baseRepository,
                           ChatRepositoryPrivate privateRepository,
                           ChatRepositoryGroup groupRepository) {
        this.baseRepository = baseRepository;
        this.privateRepository = privateRepository;
        this.groupRepository = groupRepository;
    }
    
    @Override
    public Chat createPrivate(User user, User target) {
        if (privateRepository.existsByMembersIn(Arrays.asList(user, target))) {
            throw new IllegalActionException("illegalAction.chat.private.alreadyExist", target.getId());
        }
    
        if (!target.isPublic() && !target.hasFriendship(user)) {
            throw new IllegalActionException("illegalAction.chat.private.createWithNotFriend", target.getId());
        }
    
        PrivateChat entity = new PrivateChat();
        entity.setMembers(Sets.newHashSet(user, target));
        return baseRepository.save(entity);
    }
    
    @Override
    public void deletePrivate(Long id, User user) {
        Chat entity = this.findPrivateByIdAndUser(id, user);
        baseRepository.delete(entity);
    }
    
    @Override
    public Chat createGroup(User creator, String name, Set<User> members) {
        for (User member : members) {
            if (!member.isPublic() && !member.hasFriendship(creator)) {
                throw new IllegalActionException("illegalAction.chat.group.createWithNotFriend", member.getId());
            }
        }
        
        Set<User> finalMembers = new HashSet<>(members);
        finalMembers.add(creator);
        
        GroupChat entity = new GroupChat();
        entity.setName(name);
        entity.setOwner(creator);
        entity.setMembers(finalMembers);
        return baseRepository.save(entity);
    }
    
    @Override
    public Chat updateGroup(Long id, User member, String name) {
        GroupChat entity = findGroupByIdAndUser(id, member);
        entity.setName(name);
        return baseRepository.save(entity);
    }
    
    @Override
    public Chat updateGroupMembers(Long id, Long ownerId, Set<User> members) {
        GroupChat entity = findGroupByIdAndOwnerId(id, ownerId);
        User owner = entity.getOwner();
        
        if (!members.contains(owner)) {
            throw new IllegalActionException("illegalAction.chat.group.removeOwner", id, ownerId);
        }
        
        Set<User> finalMembers = new HashSet<>();
        
        for (User member : members) {
            if (entity.hasMember(member)) {
                finalMembers.add(member);
                continue;
            }
            
            if (!member.isPublic() && !member.hasFriendship(owner)) {
                throw new IllegalActionException("illegalAction.chat.group.addNotFriend", member.getId());
            }
            
            finalMembers.add(member);
        }
        
        entity.setMembers(finalMembers);
        return baseRepository.save(entity);
    }
    
    @Override
    public Chat setOwner(Long id, Long ownerId, User newOwner) {
        GroupChat entity = findGroupByIdAndOwnerId(id, ownerId);
    
        if (!entity.hasMember(newOwner)) {
            throw new IllegalActionException("illegalAction.chat.group.setOwnerNotMember", id, newOwner.getId());
        }
    
        entity.setOwner(newOwner);
        return baseRepository.save(entity);
    }
    
    @Override
    public void leaveGroup(Long id, User member) {
        GroupChat entity = findGroupByIdAndUser(id, member);
        
        if (entity.isOwner(member)) {
            throw new IllegalActionException("illegalAction.chat.group.leaveOwner", id, member.getId());
        }
    
        Set<User> finalMembers = new HashSet<>(entity.getMembers());
        finalMembers.remove(member);
        entity.setMembers(finalMembers);
        baseRepository.save(entity);
    }
    
    @Override
    public void deleteGroup(Long id, Long ownerId) {
        GroupChat entity = findGroupByIdAndOwnerId(id, ownerId);
        baseRepository.delete(entity);
    }
    
    @Override
    public Page<User> getMembers(Long id, User user, Pageable pageable) {
        Set<User> members = this.findByIdAndUser(id, user).getMembers();
        return new PageImpl<>(new ArrayList<>(members), pageable, members.size());
    }
    
    @Override
    public Chat findByIdAndUser(Long id, User user) {
        return baseRepository.findByIdAndMembersContaining(id, user)
                .orElseThrow(() -> new NotFoundException("notFound.chat.byIdAndUser", id, user.getId()));
    }
    
    @Override
    public Page<Chat> findAllByUser(User user, Pageable pageable) {
        return baseRepository.findAllByMembersContaining(user, pageable);
    }
    
    private PrivateChat findPrivateByIdAndUser(Long id, User user) {
        return privateRepository.findByIdAndMembersContaining(id, user)
                .orElseThrow(() -> new NotFoundException("notFound.chat.private.byIdAndUser", id, user.getId()));
    }
    
    private GroupChat findGroupByIdAndUser(Long id, User user) {
        return groupRepository.findByIdAndMembersContaining(id, user)
                .orElseThrow(() -> new NotFoundException("notFound.chat.group.byIdAndUser", id, user.getId()));
    }
    
    private GroupChat findGroupByIdAndOwnerId(Long id, Long ownerId) {
        return groupRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("notFound.chat.group.byIdAndOwnerId", id, ownerId));
    }
}
