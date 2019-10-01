package com.craftmaster.lds.fgc.question;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Question  {
  @Id
  private Long id;
  @NotNull private Boolean enabled;
}
