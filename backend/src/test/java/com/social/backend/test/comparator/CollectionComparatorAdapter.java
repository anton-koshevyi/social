package com.social.backend.test.comparator;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Equator;

public class CollectionComparatorAdapter<E, T extends Collection<E>> implements Comparator<T> {

  private final Equator<E> equator;

  public CollectionComparatorAdapter(Comparator<E> comparator) {
    equator = new Equator<E>() {
      @Override
      public boolean equate(E left, E right) {
        return comparator.compare(left, right) == 0;
      }

      @Override
      public int hash(E o) {
        // Always returns 0 to trigger ::equals
        // (by hash-based collections logic)
        return 0;
      }
    };
  }

  @Override
  public int compare(T left, T right) {
    return CollectionUtils.isEqualCollection(left, right, equator) ? 0 : 1;
  }

}
