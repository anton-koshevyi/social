package com.social.backend.test.model;

public final class ModelFactory {

  private ModelFactory() {
  }

  public static <T> T createModel(ModelType<T> type) {
    Class<T> modelClass = type.modelClass();
    AbstractFactory<T> factory = FactoryProducer.getFactory(modelClass);
    return factory.createModel(type);
  }

}
