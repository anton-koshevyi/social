package com.social.backend.test.model.factory;

import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.wrapper.ModelWrapper;

abstract class AbstractFactory<T> {

  abstract ModelWrapper<T> createWrapper(ModelType<T> type);

}
