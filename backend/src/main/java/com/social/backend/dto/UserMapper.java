package com.social.backend.dto;

import org.springframework.stereotype.Component;

import com.social.backend.dto.user.ResponseDto;
import com.social.backend.model.user.User;

@Component
public class UserMapper implements ResponseMapper<User, ResponseDto> {
    @Override
    public ResponseDto map(User source) {
        if (source == null) {
            return null;
        }
        
        return new ResponseDto()
                .setId(source.getId())
                .setEmail(source.getEmail())
                .setUsername(source.getUsername())
                .setFirstName(source.getFirstName())
                .setLastName(source.getLastName())
                .setPublicity(source.getPublicity())
                .setModer(source.isModer())
                .setAdmin(source.isAdmin());
    }
    
    @Override
    public ResponseDto mapHidden(User source) {
        return this.map(source)
                .setEmail(null);
    }
}
