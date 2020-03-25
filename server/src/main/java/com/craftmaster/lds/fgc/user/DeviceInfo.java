package com.craftmaster.lds.fgc.user;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class DeviceInfo {

  private UUID deviceId;

  @Id private Instant createdAt = Instant.now();

  @Column(columnDefinition = "text")
  private String userAgent;

  @Column(columnDefinition = "text")
  private String inetAddress;

  private Instant lastLogIn;
}
