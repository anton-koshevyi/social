package com.social.backend.exception;

import javax.servlet.http.HttpServletResponse;

public class WrongCredentialsException extends LocalizedException {
    public WrongCredentialsException(String code, Object[] args, Throwable cause) {
        super(code, args, cause);
    }
    
    public WrongCredentialsException(String code) {
        super(code);
    }
    
    public WrongCredentialsException(String code, Throwable cause) {
        super(code, cause);
    }
    
    public WrongCredentialsException(String code, Object... args) {
        super(code, args);
    }
    
    @Override
    public int getStatusCode() {
        return HttpServletResponse.SC_FORBIDDEN;
    }
}
