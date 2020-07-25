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
import com.social.backend.model.conversation.Conversation;
import com.social.backend.model.conversation.GroupConversation;
import com.social.backend.model.conversation.PrivateConversation;
import com.social.backend.model.user.User;
import com.social.backend.repository.ConversationBaseRepository;
import com.social.backend.repository.ConversationGroupRepository;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationBaseRepository<Conversation> baseRepository;
    private final ConversationGroupRepository groupRepository;
    
    @Autowired
    public ConversationServiceImpl(ConversationBaseRepository<Conversation> baseRepository,
                                   ConversationGroupRepository groupRepository) {
        this.baseRepository = baseRepository;
        this.groupRepository = groupRepository;
    }
    
    @Override
    public Conversation createPrivate(User user, User target) {
        if (!target.isPublic() && !target.hasFriendship(user)) {
            throw new IllegalActionException("illegalAction.conversation.privateWithNotFriend");
        }
        
        Conversation entity = new PrivateConversation()
                .setMembers(Arrays.asList(user, target));
        return baseRepository.save(entity);
    }
    
    @Override
    public Conversation createGroup(User user, String name, List<User> members) {
        for (User member : members) {
            if (!member.isPublic() && !member.hasFriendship(user)) {
                throw new IllegalActionException("illegalAction.conversation.createGroupWithNotFriend");
            }
        }
        
        Conversation entity = new GroupConversation()
                .setName(name)
                .setOwner(user)
                .setMembers(new ArrayList<>(members));
        return baseRepository.save(entity);
    }
    
    @Override
    public Conversation updateGroup(Long id, User user, String name, List<User> newMembers) {
        GroupConversation entity = findGroupByIdAndUser(id, user);
        List<User> finalMembers = new ArrayList<>(entity.getMembers());
        
        for (User member : newMembers) {
            if (!member.isPublic() && !member.hasFriendship(user)) {
                throw new IllegalActionException("illegalAction.conversation.updateGroupWithNotFriend");
            }
            
            if (entity.hasMember(member)) {
                throw new IllegalActionException("illegalAction.conversation.addExistentMember");
            }
            
            finalMembers.add(member);
        }
        
        entity.setName(name)
                .setMembers(finalMembers);
        return baseRepository.save(entity);
    }
    
    @Override
    public void deletePrivate(Long id, User user) {
        Conversation entity = this.findByIdAndUser(id, user);
        baseRepository.delete(entity);
    }
    
    @Override
    public void leaveGroup(Long id, User user) {
        GroupConversation entity = findGroupByIdAndUser(id, user);
        this.removeGroupMembers(id, entity.getOwner().getId(), Collections.singletonList(user));
    }
    
    @Override
    public void removeGroupMembers(Long id, Long ownerId, List<User> members) {
        GroupConversation entity = findGroupByIdAndOwnerId(id, ownerId);
        List<User> finalMembers = new ArrayList<>(entity.getMembers());
        
        for (User memberToRemove : members) {
            for (int i = 0; i < finalMembers.size(); i++) {
                User member = finalMembers.get(i);
                
                if (Objects.equals(memberToRemove.getId(), member.getId())) {
                    if (entity.isOwner(member)) {
                        throw new IllegalActionException("illegalAction.conversation.removeOwner");
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
        GroupConversation entity = findGroupByIdAndOwnerId(id, ownerId);
        baseRepository.delete(entity);
    }
    
    @Override
    public Conversation setOwner(Long id, Long ownerId, User newOwner) {
        GroupConversation entity = findGroupByIdAndOwnerId(id, ownerId);
        entity.setOwner(newOwner);
        return baseRepository.save(entity);
    }
    
    @Override
    public Conversation findByIdAndUser(Long id, User user) {
        return baseRepository.findByIdAndMembersContaining(id, user)
                .orElseThrow(() -> new NotFoundException("notFound.conversation.byIdAndUser", id, user.getId()));
    }
    
    @Override
    public Page<Conversation> findAllByUser(User user, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return baseRepository.findAllByMembersContaining(user, pageable);
    }
    
    private GroupConversation findGroupByIdAndUser(Long id, User user) {
        Conversation entity = this.findByIdAndUser(id, user);
        Class<? extends Conversation> entityClass = entity.getClass();
        
        if (!GroupConversation.class.isAssignableFrom(entityClass)) {
            throw new IllegalStateException("Type is not assignable: " + entityClass);
        }
        
        return (GroupConversation) entity;
    }
    
    private GroupConversation findGroupByIdAndOwnerId(Long id, Long ownerId) {
        return groupRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("notFound.conversation.group.byIdAndOwnerId", id, ownerId));
    }
}
