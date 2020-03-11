package com.craftmaster.lds.fgc.db;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperHolderBuilder {

  public static final ObjectMapperHolderBuilder get() {
    return new ObjectMapperHolderBuilder();
  }

  public static final void clear() {
    ObjectMapperHolder holder = new ObjectMapperHolder(null);
    holder.init();
  }

  private ObjectMapperHolderBuilder() {}

  public ObjectMapperHolder build() {
    ObjectMapperHolder holder = new ObjectMapperHolder(objectMapper);
    holder.init();
    return holder;
  }

  private ObjectMapper objectMapper;

  public ObjectMapperHolderBuilder withObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    return this;
  }
}
