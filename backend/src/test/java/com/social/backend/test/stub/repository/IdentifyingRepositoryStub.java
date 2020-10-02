package com.social.backend.test.stub.repository;

import java.util.Objects;

import com.social.backend.test.stub.repository.identification.Identification;

abstract class IdentifyingRepositoryStub<ID, T> extends AbstractRepositoryStub<ID, T> {

  private final Identification<T> identification;

  IdentifyingRepositoryStub(Identification<T> identification) {
    this.identification = identification;
  }

  protected abstract ID getId(T entity);

  @Override
  public T save(ID id, T entity) {
    Objects.requireNonNull(entity, "Entity must not be null");

    if (id == null || !super.exists(id)) {
      identification.apply(entity);
      ID entityId = this.getId(entity);
      logger.debug("Applied id '{}' to entity: {}", entityId, entity);
      return super.save(entityId, entity);
    }

    return super.save(id, entity);
  }

  public T save(T entity) {
    ID id = this.getId(entity);
    return this.save(id, entity);
  }

  public void delete(T entity) {
    Objects.requireNonNull(entity, "Entity must not be null");
    ID id = this.getId(entity);
    super.delete(id, entity);
  }

}
