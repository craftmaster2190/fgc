package com.craftmaster.lds.fgc.user;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateUserRequest {

  @NotBlank
  private UUID deviceId;
}
