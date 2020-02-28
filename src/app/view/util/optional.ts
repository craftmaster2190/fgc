type Nullable<T> = T | null | undefined;

class Optional<T> {
  constructor(public readonly value: Nullable<T>) {}

  public static of<T>(value?: Nullable<T>): Optional<T> {
    return new Optional(value);
  }

  public static empty<T>(): Optional<T> {
    return Optional.of();
  }

  public isPresent(): boolean {
    return this.value != null;
  }

  public map<N>(mapper: (value: T) => Nullable<N>): Optional<N> {
    if (this.isPresent()) {
      return Optional.of(mapper(this.value));
    }

    return Optional.empty();
  }

  public filter(predicate: (value: T) => boolean): Optional<T> {
    if (this.isPresent() && predicate(this.value)) {
      return this;
    }

    return Optional.empty();
  }

  public orElse(defaultValue?: Nullable<T>): Nullable<T> {
    if (this.isPresent()) {
      return this.value;
    }

    return defaultValue;
  }

  public orElseGet(defaultValueSupplier: () => Nullable<T>): Nullable<T> {
    if (this.isPresent()) {
      return this.value;
    }

    return defaultValueSupplier();
  }
}

export { Optional };
