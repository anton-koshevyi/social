package com.social.backend.test.model.wrapper;

import java.util.function.Consumer;

public abstract class AbstractWrapper<T> implements ModelWrapper<T> {

  private final T model;

  protected AbstractWrapper(T model) {
    this.model = model;
  }

  @Override
  public T getModel() {
    return model;
  }

  @Override
  public ModelWrapper<T> with(Consumer<T> mutator) {
    mutator.accept(model);
    return this;
  }

}
