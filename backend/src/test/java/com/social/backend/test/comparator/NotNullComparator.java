package com.social.backend.test.comparator;

import java.util.Comparator;

public final class NotNullComparator<T> implements Comparator<T> {

  private static final NotNullComparator<?> LEFT = new NotNullComparator<>(true, false);
  private static final NotNullComparator<?> RIGHT = new NotNullComparator<>(false, true);
  private static final NotNullComparator<?> BOTH = new NotNullComparator<>(true, true);

  private final boolean leftNotNull;
  private final boolean rightNotNull;

  private NotNullComparator(boolean leftNotNull, boolean rightNotNull) {
    this.leftNotNull = leftNotNull;
    this.rightNotNull = rightNotNull;
  }

  public static <T> NotNullComparator<T> leftNotNull() {
    return (NotNullComparator<T>) LEFT;
  }

  public static <T> NotNullComparator<T> rightNotNull() {
    return (NotNullComparator<T>) RIGHT;
  }

  public static <T> NotNullComparator<T> bothNotNull() {
    return (NotNullComparator<T>) BOTH;
  }

  @Override
  public int compare(T left, T right) {
    boolean result;

    if (leftNotNull && rightNotNull) {
      result = (left != null) && (right != null);
    } else {
      if (leftNotNull) {
        result = (left != null);
      } else {
        result = (right != null);
      }
    }

    return result ? 0 : 1;
  }

}
