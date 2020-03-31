package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateUserRequest {
  @JsonDeserialize(using = StringTrimDeserializer.class)
  private String newName;

  private UUID userId;
}
