package com.craftmaster.lds.fgc.config;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Predicates {
  ;

  public static <T> Predicate<T> not(Function<T, Boolean> predicate) {
    return (T t) -> {
      var bool = predicate.apply(t);
      return bool == null || !bool;
    };
  }
}
