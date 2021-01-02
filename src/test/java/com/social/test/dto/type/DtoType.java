package com.social.test.dto.type;

public interface DtoType<T> {

  String name();

  Class<T> dtoClass();

}
