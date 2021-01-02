package com.social.test.dto.wrapper;

import java.util.function.Consumer;

import com.social.test.model.wrapper.ModelWrapper;

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
