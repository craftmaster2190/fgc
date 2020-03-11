package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PatchUserRequest {

  @JsonDeserialize(using = StringTrimDeserializer.class)
  private String name;

  @JsonDeserialize(using = StringTrimDeserializer.class)
  private String family;
}
