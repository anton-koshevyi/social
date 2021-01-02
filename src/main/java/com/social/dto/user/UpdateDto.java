package com.social.dto.user;

import lombok.Getter;
import lombok.Setter;

import com.social.constraint.Email;
import com.social.constraint.FirstName;
import com.social.constraint.LastName;
import com.social.constraint.Publicity;
import com.social.constraint.Username;

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
