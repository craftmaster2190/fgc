package com.craftmaster.lds.fgc.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class Family {
  @Id
  @GeneratedValue
  private Long id;

  @NotBlank
  private String name;
}
