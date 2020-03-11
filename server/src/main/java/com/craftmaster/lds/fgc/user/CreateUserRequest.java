package com.craftmaster.lds.fgc.user;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequest extends PatchUserRequest {

  @NotNull private UUID deviceId;
}
