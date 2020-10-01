package com.social.backend.test.model;

import com.google.common.reflect.TypeToken;

public final class ModelFactory {

  private ModelFactory() {
  }

  public static <T> T createModel(ModelType<T> type) {
    Class<T> modelClass = resolveModelClass(type);
    AbstractFactory<T> factory = FactoryProducer.getFactory(modelClass);
    return factory.createModel(type);
  }

  private static <T> Class<T> resolveModelClass(ModelType<T> type) {
    return (Class<T>) TypeToken.of(type.getClass())
        .resolveType(ModelType.class.getTypeParameters()[0])
        .getRawType();
  }

}
