package com.social.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.Email;
import com.social.backend.constraint.FirstName;
import com.social.backend.constraint.LastName;
import com.social.backend.constraint.Publicity;
import com.social.backend.constraint.Username;

@Getter
@Setter
public class UpdateDto {

  @Email
  private String email;

  @Username
  private String username;

  @FirstName
  private String firstName;

  @LastName
  private String lastName;

  @Publicity
  private Integer publicity;

}
