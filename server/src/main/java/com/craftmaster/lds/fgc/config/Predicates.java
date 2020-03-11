package com.craftmaster.lds.fgc.config;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Predicates {
  ;

  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return predicate.negate();
  }

  public static <T> Predicate<T> isTrue(Function<T, Boolean> mapper) {
    return predicate(mapper, bool -> Objects.equals(bool, true));
  }

  public static <T, R> Predicate<T> nonNull(Function<T, R> mapper) {
    return predicate(mapper, Objects::nonNull);
  }

  public static <T, R> Predicate<T> predicate(Function<T, R> mapper, Predicate<R> predicate) {
    return (t) -> predicate.test(mapper.apply(t));
  }

  public static <T> Predicate<T> all(Predicate<T>... predicates) {
    return (t) -> Arrays.stream(predicates).allMatch(predicate -> predicate.test(t));
  }
}
