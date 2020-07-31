package com.social.backend.dto;

import org.springframework.stereotype.Component;

import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.User;

@Component
public class UserMapper implements EntityMapper<User, UserDto> {
    @Override
    public UserDto map(User source) {
        if (source == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(source.getId());
        dto.setEmail(source.getEmail());
        dto.setUsername(source.getUsername());
        dto.setFirstName(source.getFirstName());
        dto.setLastName(source.getLastName());
        dto.setPublicity(source.getPublicity());
        dto.setModer(source.isModer());
        dto.setAdmin(source.isAdmin());
        return dto;
    }
    
    @Override
    public UserDto mapHidden(User source) {
        UserDto dto = this.map(source);
        dto.setEmail(null);
        return dto;
    }
}
