package com.social.backend.exception;

import javax.servlet.http.HttpServletResponse;

public class IllegalActionException extends LocalizedException {
  
  public IllegalActionException(String code, Object[] args, Throwable cause) {
    super(code, args, cause);
  }
  
  public IllegalActionException(String code) {
    super(code);
  }
  
  public IllegalActionException(String code, Throwable cause) {
    super(code, cause);
  }
  
  public IllegalActionException(String code, Object... args) {
    super(code, args);
  }
  
  @Override
  public int getStatusCode() {
    return HttpServletResponse.SC_FORBIDDEN;
  }
  
}
