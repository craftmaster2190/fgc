package com.craftmaster.lds.fgc.user;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class LoginUserRequest {

  @NotNull
  private UUID userId;
  @NotNull
  private UUID deviceId;
}
