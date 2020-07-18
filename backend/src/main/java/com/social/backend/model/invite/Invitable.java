package com.social.backend.model.invite;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "invites")
public interface Invitable {
    @Id
    Long getId();
    
    Invitable setId(Long id);
}
