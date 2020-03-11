package com.craftmaster.lds.fgc.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjectMapperHolder {

  private static volatile ObjectMapper OBJECT_MAPPER;
  private final ObjectMapper objectMapper;

  public static ObjectMapper get() {
    return OBJECT_MAPPER;
  }

  @PostConstruct
  public void init() {
    OBJECT_MAPPER = objectMapper;
  }
}
