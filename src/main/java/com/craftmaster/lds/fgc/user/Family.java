package com.craftmaster.lds.fgc.user;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Entity
@Accessors(chain = true)
public class Family {

  @Id
  @GeneratedValue
  private UUID id;

  @NotBlank
  private String name;
}
