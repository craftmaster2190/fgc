package com.craftmaster.lds.fgc.answer;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Data
@Accessors(chain = true)
public class Score {

  @Id
  private UUID userOrFamilyId;

  @NotNull
  private long score;

  @NotNull
  private Instant updatedAt = Instant.now();

  @Transient
  public boolean isValid() {
    return getUpdatedAt().plus(1, ChronoUnit.MINUTES).isAfter(Instant.now());
  }
}
