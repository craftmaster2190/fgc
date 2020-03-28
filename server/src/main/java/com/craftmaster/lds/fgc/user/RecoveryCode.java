package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class RecoveryCode {
  private Instant createdAt = Instant.now();

  private UUID userId;

  @Id
  @Column(columnDefinition = "text")
  private String code = randomCode();

  @JsonIgnore
  @Transient
  public boolean isValid() {
    return getCreatedAt().plus(30, ChronoUnit.MINUTES).isAfter(Instant.now());
  }

  public static String randomCode() {
    return new Random()
        .ints(6, 0, 26)
        .mapToObj(i -> Character.toString((char) (i + 'A')))
        .collect(Collectors.joining());
  }
}
