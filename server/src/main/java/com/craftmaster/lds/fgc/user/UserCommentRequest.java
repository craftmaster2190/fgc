package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserCommentRequest extends PatchUserRequest {

  @JsonDeserialize(using = StringTrimDeserializer.class)
  private String userComment;
}
