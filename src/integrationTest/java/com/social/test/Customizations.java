package com.social.test;

import org.skyscreamer.jsonassert.Customization;

public final class Customizations {

  private Customizations() {
  }

  public static Customization notNullActual(String path) {
    return new Customization(path, (actual, expected) -> actual != null);
  }

}
