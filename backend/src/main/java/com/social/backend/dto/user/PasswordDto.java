package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

import com.social.backend.constraint.FieldMatch;
import com.social.backend.constraint.Password;

@FieldMatch(notMatch = true, field = "actual", compared = "change")
@FieldMatch(field = "change", compared = "confirm")
public class PasswordDto {
  
  @NotNull
  @Password
  private String actual;
  
  @NotNull
  @Password
  private String change;
  private String confirm;
  
  public PasswordDto setActual(String actual) {
    this.actual = actual;
    return this;
  }
  
  public PasswordDto setChange(String change) {
    this.change = change;
    return this;
  }
  
  public PasswordDto setConfirm(String confirm) {
    this.confirm = confirm;
    return this;
  }
  
  public String getActual() {
    return actual;
  }
  
  public String getChange() {
    return change;
  }
  
  public String getConfirm() {
    return confirm;
  }
  
}
