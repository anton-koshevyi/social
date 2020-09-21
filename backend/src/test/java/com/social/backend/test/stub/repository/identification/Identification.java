package com.social.backend.test.stub.repository.identification;

@FunctionalInterface
public interface Identification<T> {

  void apply(T entity);

}
