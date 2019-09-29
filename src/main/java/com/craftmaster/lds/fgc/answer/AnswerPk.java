package com.craftmaster.lds.fgc.answer;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Embeddable
public class AnswerPk implements Serializable {

  @NotNull
  private Long userId;

  @NotNull
  private Long questionId;
}
