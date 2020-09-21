package com.social.backend.test.stub.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractRepositoryStub<ID, T> {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final Map<ID, T> entities = new LinkedHashMap<>();

  public T save(ID id, T entity) {
    Objects.requireNonNull(id, "Id must not be null");
    Objects.requireNonNull(entity, "Entity must not be null");
    boolean isNew = null == entities.put(id, entity);
    logger.debug("Entity with id '{}' {}: {} ", id, (isNew ? "added" : "updated"), entity);
    return entity;
  }

  public T find(ID id) {
    Objects.requireNonNull(id, "Id must not be null");
    T entity = entities.get(id);
    logger.debug("Entity with id '{}' {}found", id, (entity == null ? "not " : ""));
    return entity;
  }

  public T find(Predicate<T> criteria) {
    Objects.requireNonNull(criteria, "Criteria must not be null");
    return entities.values()
        .stream()
        .filter(criteria)
        .findFirst()
        .orElse(null);
  }

  public boolean exists(ID id) {
    Objects.requireNonNull(id, "Id must not be null");
    boolean exists = entities.containsKey(id);
    logger.debug("Entity with id '{}' {}exists", id, (exists ? "" : "not "));
    return exists;
  }

  public boolean exists(Predicate<T> criteria) {
    Objects.requireNonNull(criteria, "Criteria must not be null");
    return entities.values()
        .stream()
        .anyMatch(criteria);
  }

  public void delete(ID id, T entity) {
    Objects.requireNonNull(id, "Id must not be null");
    Objects.requireNonNull(entity, "Entity must not be null");
    boolean removed = entities.remove(id, entity);
    logger.debug("Entity with id '{}' {}removed: {}", id, (removed ? "" : "not "), entity);
  }

  public void deleteAll() {
    int size = entities.size();
    entities.clear();
    logger.debug("Delete {} entities", size);
  }

  public int size() {
    int size = entities.size();
    logger.debug("Repository size: {}", size);
    return size;
  }

  public List<T> findAll() {
    Collection<T> all = entities.values();
    logger.debug("{} entities found", all.size());
    return new ArrayList<>(all);
  }

}
