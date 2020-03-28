package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class RecoveryRequest {
  @JsonIgnore @Id private Instant createdAt = Instant.now();

  private UUID userId;

  private UUID deviceId;
}
