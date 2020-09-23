package com.social.backend.test.model;

public abstract class ModelFactory<T> {

  public abstract T createModel(ModelType<T> type);

}
