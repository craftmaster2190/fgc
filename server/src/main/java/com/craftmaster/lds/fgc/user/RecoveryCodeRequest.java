package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class RecoveryCodeRequest extends PatchUserRequest {

  @NotBlank
  @JsonDeserialize(using = StringTrimDeserializer.class)
  private String recoveryCode;
}
