package com.social.backend.test.model;

import java.util.HashMap;
import java.util.Map;

public final class ModelFactoryProducer {

  private static final Map<String, ModelFactory<?>> typeFactories = new HashMap<>();

  private ModelFactoryProducer() {
  }

  public static <T> ModelFactory<T> getFactory(Class<T> type) {
    if (type == null) {
      return null;
    }

    String typeName = type.getName();

    if (!typeFactories.containsKey(typeName)) {
    }

    return (ModelFactory<T>) typeFactories.get(typeName);
  }

}
