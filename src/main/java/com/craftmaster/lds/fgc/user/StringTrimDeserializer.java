package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class StringTrimDeserializer extends JsonDeserializer<String> {

  @Override
  public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
    throws IOException {
    return Optional.ofNullable(jsonParser.getText())
      .map(String::trim)
      .filter(StringUtils::hasText)
      .orElse(null);
  }
}
