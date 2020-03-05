package com.craftmaster.lds.fgc.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequest extends PatchUserRequest {

  @NotNull
  private UUID deviceId;
}
