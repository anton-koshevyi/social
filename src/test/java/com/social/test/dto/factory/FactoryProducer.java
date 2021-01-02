package com.social.test.dto.factory;

import java.util.HashMap;
import java.util.Map;

final class FactoryProducer {

  private static final Map<String, AbstractFactory<?>> typeFactories = new HashMap<>();

  private FactoryProducer() {
  }

  static <T> AbstractFactory<T> getFactory(Class<T> type) {
    String typeName = type.getName();

    if (!typeFactories.containsKey(typeName)) {

    }

    return (AbstractFactory<T>) typeFactories.get(typeName);
  }

}
