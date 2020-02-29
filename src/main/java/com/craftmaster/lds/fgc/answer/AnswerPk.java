package com.craftmaster.lds.fgc.answer;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class AnswerPk implements Serializable {

  private static final long serialVersionUID = 20190930L;

  @NotNull
  private UUID userId;

  @NotNull
  private Long questionId;
}
