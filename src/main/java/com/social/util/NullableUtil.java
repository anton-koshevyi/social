package com.social.util;

import java.util.Objects;
import java.util.function.Consumer;

public final class NullableUtil {

  private NullableUtil() {
  }

  public static <T> boolean set(Consumer<T> setter, T value) {
    Objects.requireNonNull(setter, "Setter must not be null");

    if (value != null) {
      setter.accept(value);
      return true;
    }

    return false;
  }

}
