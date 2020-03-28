package com.craftmaster.lds.fgc.user;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Accessors(chain = true)
public class RecoveryRequest {
  @Id
  private Instant createdAt = Instant.now();

  private UUID userId;

  private UUID deviceId;
}
