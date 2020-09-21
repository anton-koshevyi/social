package com.social.backend.test.stub.repository.identification;

public class IdentificationContext<T> implements Identification<T> {

  private Identification<T> strategy;

  public void setStrategy(Identification<T> strategy) {
    this.strategy = strategy;
  }

  @Override
  public void apply(T entity) {
    strategy.apply(entity);
  }

}
