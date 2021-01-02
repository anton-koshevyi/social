package com.social.test.model.wrapper;

import java.util.function.Consumer;

public interface ModelWrapper<T> {

  T getModel();

  ModelWrapper<T> with(Consumer<T> mutator);

}
