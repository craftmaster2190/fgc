package com.craftmaster.lds.fgc.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class ConfigProperty {

  @Id
  @Column(columnDefinition = "text")
  private String key;

  @Column(columnDefinition = "text")
  private String value;
}
