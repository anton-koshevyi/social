package com.social.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepositoryBase;
import com.social.backend.repository.ChatRepositoryGroup;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepositoryBase<Chat> baseRepository;
    private final ChatRepositoryGroup groupRepository;
    
    @Autowired
    public ChatServiceImpl(ChatRepositoryBase<Chat> baseRepository,
                           ChatRepositoryGroup groupRepository) {
        this.baseRepository = baseRepository;
        this.groupRepository = groupRepository;
    }
    
    @Override
    public Chat createPrivate(User user, User target) {
        if (!target.isPublic() && !target.hasFriendship(user)) {
            throw new IllegalActionException("illegalAction.chat.private.createWithNotFriend");
        }
        
        Chat entity = new PrivateChat()
                .setMembers(Arrays.asList(user, target));
        return baseRepository.save(entity);
    }
    
    @Override
    public Chat createGroup(User user, String name, List<User> members) {
        for (User member : members) {
            if (!member.isPublic() && !member.hasFriendship(user)) {
                throw new IllegalActionException("illegalAction.chat.group.createWithNotFriend");
            }
        }
        
        Chat entity = new GroupChat()
                .setName(name)
                .setOwner(user)
                .setMembers(new ArrayList<>(members));
        return baseRepository.save(entity);
    }
    
    @Override
    public Chat updateGroup(Long id, User user, String name, List<User> newMembers) {
        GroupChat entity = findGroupByIdAndUser(id, user);
        List<User> finalMembers = new ArrayList<>(entity.getMembers());
        
        for (User newMember : newMembers) {
            if (entity.hasMember(newMember)) {
                throw new IllegalActionException("illegalAction.chat.group.addExistentMember");
            }
            
            if (!newMember.isPublic() && !newMember.hasFriendship(user)) {
                throw new IllegalActionException("illegalAction.chat.group.addNotFriend");
            }
        
            finalMembers.add(newMember);
        }
    
        entity.setName(name)
                .setMembers(finalMembers);
        return baseRepository.save(entity);
    }
    
    @Override
    public void deletePrivate(Long id, User user) {
        Chat entity = this.findByIdAndUser(id, user);
        baseRepository.delete(entity);
    }
    
    @Override
    public void leaveGroup(Long id, User user) {
        GroupChat entity = findGroupByIdAndUser(id, user);
        this.removeGroupMembers(id, entity.getOwner().getId(), Collections.singletonList(user));
    }
    
    @Override
    public void removeGroupMembers(Long id, Long ownerId, List<User> members) {
        GroupChat entity = findGroupByIdAndOwnerId(id, ownerId);
        List<User> finalMembers = new ArrayList<>(entity.getMembers());
        
        for (User memberToRemove : members) {
            for (int i = 0; i < finalMembers.size(); i++) {
                User member = finalMembers.get(i);
                
                if (Objects.equals(memberToRemove.getId(), member.getId())) {
                    if (entity.isOwner(member)) {
                        throw new IllegalActionException("illegalAction.chat.group.removeOwner");
                    }
                    
                    finalMembers.remove(i);
                    break;
                }
            }
        }
        
        entity.setMembers(finalMembers);
        baseRepository.save(entity);
    }
    
    @Override
    public void deleteGroup(Long id, Long ownerId) {
        GroupChat entity = findGroupByIdAndOwnerId(id, ownerId);
        baseRepository.delete(entity);
    }
    
    @Override
    public Chat setOwner(Long id, Long ownerId, User newOwner) {
        GroupChat entity = findGroupByIdAndOwnerId(id, ownerId);
        entity.setOwner(newOwner);
        return baseRepository.save(entity);
    }
    
    @Override
    public Chat findByIdAndUser(Long id, User user) {
        return baseRepository.findByIdAndMembersContaining(id, user)
                .orElseThrow(() -> new NotFoundException("notFound.chat.byIdAndUser", id, user.getId()));
    }
    
    @Override
    public Page<Chat> findAllByUser(User user, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return baseRepository.findAllByMembersContaining(user, pageable);
    }
    
    private GroupChat findGroupByIdAndUser(Long id, User user) {
        Chat entity = this.findByIdAndUser(id, user);
        Class<? extends Chat> entityClass = entity.getClass();
        
        if (!GroupChat.class.isAssignableFrom(entityClass)) {
            throw new IllegalStateException("Type is not assignable: " + entityClass);
        }
        
        return (GroupChat) entity;
    }
    
    private GroupChat findGroupByIdAndOwnerId(Long id, Long ownerId) {
        return groupRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("notFound.chat.group.byIdAndOwnerId", id, ownerId));
    }
}
