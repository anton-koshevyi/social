package com.social.test.stub;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;

public final class PasswordEncoderStub implements PasswordEncoder {

  public static final String ENCODED_PREFIX = "{encoded}";

  private static PasswordEncoderStub instance;

  private PasswordEncoderStub() {
  }

  public static PasswordEncoderStub getInstance() {
    if (instance == null) {
      synchronized (PasswordEncoderStub.class) {
        if (instance == null) {
          instance = new PasswordEncoderStub();
        }
      }
    }

    return instance;
  }

  @Override
  public String encode(CharSequence rawPassword) {
    Objects.requireNonNull(rawPassword, "Raw password must not be null");
    return ENCODED_PREFIX + rawPassword;
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    Objects.requireNonNull(rawPassword, "Raw password must not be null");
    Objects.requireNonNull(encodedPassword, "Encoded password must not be null");

    if (!encodedPassword.startsWith(ENCODED_PREFIX)) {
      throw new IllegalArgumentException("Password is not encoded: " + encodedPassword);
    }

    String decoded = encodedPassword.replace(ENCODED_PREFIX, "");
    return decoded.equals(rawPassword.toString());
  }

}
