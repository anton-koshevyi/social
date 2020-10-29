package com.social.backend.test.model.factory;

import java.util.function.Consumer;

import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.wrapper.ModelWrapper;

public final class ModelFactory {

  private ModelFactory() {
  }

  public static <T> T createModel(ModelType<T> type) {
    return createWrapper(type).getModel();
  }

  @SafeVarargs
  public static <T> T createModelMutating(ModelType<T> type, Consumer<T>... mutators) {
    ModelWrapper<T> wrapper = createWrapper(type);

    for (Consumer<T> mutator : mutators) {
      wrapper.with(mutator);
    }

    return wrapper.getModel();
  }

  private static <T> ModelWrapper<T> createWrapper(ModelType<T> type) {
    Class<T> modelClass = type.modelClass();
    AbstractFactory<T> factory = FactoryProducer.getFactory(modelClass);
    return factory.createWrapper(type);
  }

}
