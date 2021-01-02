package com.social.test.model.type;

public interface ModelType<T> {

  String name();

  Class<T> modelClass();

}
