package com.social.backend.mapper.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMapper<T> {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  public abstract <R> R map(T model, Class<R> dtoType);

}
