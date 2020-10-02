package com.social.backend.test.model;

public abstract class AbstractFactory<T> {

  public abstract T createModel(ModelType<T> type);

}
