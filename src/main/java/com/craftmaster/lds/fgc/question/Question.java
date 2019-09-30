package com.craftmaster.lds.fgc.question;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Question  implements Serializable {
  @Id
  private Long id;
  @NotNull private Boolean enabled;
}
