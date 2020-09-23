package com.social.backend.test.model;

import java.util.HashMap;
import java.util.Map;

import com.social.backend.model.user.User;
import com.social.backend.test.model.user.UserFactory;

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
      if (User.class.equals(type)) {
        typeFactories.put(typeName, new UserFactory());
      }
    }

    return (ModelFactory<T>) typeFactories.get(typeName);
  }

}
