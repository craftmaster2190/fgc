package com.craftmaster.lds.fgc.answer;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Embeddable
public class AnswerPk {

  @NotNull
  private Long userId;

  @NotNull
  private Long questionId;
}
