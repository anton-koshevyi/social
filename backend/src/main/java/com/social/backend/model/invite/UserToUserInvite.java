package com.social.backend.model.invite;

import javax.persistence.AssociationOverride;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import com.social.backend.model.user.User;

@Entity
@DiscriminatorValue("user_to_user")
@AssociationOverride(name = "receiver", joinColumns = @JoinColumn(name = "user_id"))
@AssociationOverride(name = "sender", joinColumns = @JoinColumn(name = "user_id"))
public class UserToUserInvite extends Invite<User, User> {
}
