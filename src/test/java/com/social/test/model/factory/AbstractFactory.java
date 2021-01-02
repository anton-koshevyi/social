package com.social.test.model.factory;

import com.social.test.model.type.ModelType;
import com.social.test.model.wrapper.ModelWrapper;

abstract class AbstractFactory<T> {

  abstract ModelWrapper<T> createWrapper(ModelType<T> type);

}
