package com.social.test.dto.factory;

import com.social.test.dto.type.DtoType;
import com.social.test.dto.wrapper.DtoWrapper;

abstract class AbstractFactory<T> {

  abstract DtoWrapper<T> createWrapper(DtoType<T> type);

}
