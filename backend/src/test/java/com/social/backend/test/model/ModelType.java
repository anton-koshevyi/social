package com.social.backend.test.model;

public interface ModelType<T> {

  String name();

  Class<T> modelClass();

}
