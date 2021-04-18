package com.social.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class NullableUtilsTest {

  @Test
  public void set_whenNullSetter_expectException() {
    Assertions
        .assertThatThrownBy(() -> NullableUtils.set(null, "new value"))
        .isExactlyInstanceOf(NullPointerException.class)
        .hasMessage("Setter must not be null");
  }

  @Test
  public void set_whenNullValue_expectFalseAndNoChanges() {
    TestObject object = new TestObject();
    object.setField("value");

    Assertions
        .assertThat(NullableUtils.set(object::setField, null))
        .isFalse();
    Assertions
        .assertThat(object.getField())
        .isEqualTo("value");
  }

  @Test
  public void set() {
    TestObject object = new TestObject();
    object.setField("value");

    Assertions
        .assertThat(NullableUtils.set(object::setField, "new value"))
        .isTrue();
    Assertions
        .assertThat(object.getField())
        .isEqualTo("new value");
  }


  private static class TestObject {

    private String field;

    public void setField(String field) {
      this.field = field;
    }

    public String getField() {
      return field;
    }

  }

}
