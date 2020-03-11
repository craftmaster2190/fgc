package com.craftmaster.lds.fgc.user;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginUserRequest {

  @NotNull private UUID userId;
  @NotNull private UUID deviceId;
}
