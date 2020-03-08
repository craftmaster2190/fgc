package com.craftmaster.lds.fgc.chat;

import java.time.Instant;
import lombok.Data;

@Data
public class InstantDeserializer {
  private long epochSecond;
  private long nano;

  public Instant toInstant() {
    return Instant.ofEpochSecond(getEpochSecond(), getNano());
  }
}
