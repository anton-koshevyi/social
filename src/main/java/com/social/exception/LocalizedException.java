package com.social.exception;

import org.springframework.context.MessageSourceResolvable;

public abstract class LocalizedException
    extends RuntimeException
    implements MessageSourceResolvable {
  
  private final String code;
  private final Object[] args;
  
  public LocalizedException(String code, Object[] args, Throwable cause) {
    super(cause);
    this.code = code;
    this.args = args;
  }
  
  public LocalizedException(String code) {
    this(code, null, null);
  }
  
  public LocalizedException(String code, Throwable cause) {
    this(code, null, cause);
  }
  
  public LocalizedException(String code, Object... args) {
    this(code, args, null);
  }
  
  public abstract int getStatusCode();
  
  @Override
  public final String[] getCodes() {
    return new String[]{code};
  }
  
  @Override
  public final Object[] getArguments() {
    return args;
  }
  
  @Override
  public final String getDefaultMessage() {
    return null;
  }
  
}
