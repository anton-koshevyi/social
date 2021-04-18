package com.social.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SharedContext {

  private static final ThreadLocal<Map<String, Object>> contextHolder =
      ThreadLocal.withInitial(ConcurrentHashMap::new);

  private SharedContext() {
  }

  public static void put(String key, Object object) {
    contextHolder.get().put(key, object);
  }

  public static <T> T get(String key, Class<T> type) {
    return type.cast(contextHolder.get().get(key));
  }

  public static <T> T get(String key) {
    return (T) contextHolder.get().get(key);
  }

  public static void clear() {
    contextHolder.get().clear();
  }

}
