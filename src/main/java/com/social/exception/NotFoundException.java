package com.social.exception;

import javax.servlet.http.HttpServletResponse;

public class NotFoundException extends LocalizedException {
  
  public NotFoundException(String code, Object[] args, Throwable cause) {
    super(code, args, cause);
  }
  
  public NotFoundException(String code) {
    super(code);
  }
  
  public NotFoundException(String code, Throwable cause) {
    super(code, cause);
  }
  
  public NotFoundException(String code, Object... args) {
    super(code, args);
  }
  
  @Override
  public int getStatusCode() {
    return HttpServletResponse.SC_NOT_FOUND;
  }
  
}
