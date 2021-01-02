package com.social.test.dto.wrapper;

import java.util.function.Consumer;

public interface DtoWrapper<T> {

  T getDto();

  DtoWrapper<T> with(Consumer<T> mutator);

}
