package com.social.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  
  private Long id;
  private String email;
  private String username;
  private String firstName;
  private String lastName;
  private Integer publicity;
  private Boolean moder;
  private Boolean admin;
  
}
