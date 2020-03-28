package com.craftmaster.lds.fgc.answer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Score {

  @Id private UUID userOrFamilyId;

  @NotNull private long score;

  @NotNull private Instant updatedAt = Instant.now();

  @JsonIgnore
  @Transient
  public boolean isValid() {
    return getUpdatedAt().plus(1, ChronoUnit.MINUTES).isAfter(Instant.now());
  }
}
