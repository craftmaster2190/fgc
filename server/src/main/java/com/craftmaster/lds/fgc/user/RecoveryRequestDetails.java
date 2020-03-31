package com.craftmaster.lds.fgc.user;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class RecoveryRequestDetails {
  @Id private Instant createdAt = Instant.now();

  private UUID userId;
  private User user;

  private UUID deviceId;
  private Set<DeviceInfo> deviceInfos;

  private String userComment;

  private Set<DeviceInfo> usersOtherDeviceInfos;

  public RecoveryRequestDetails(RecoveryRequest recoveryRequest) {
    createdAt = recoveryRequest.getCreatedAt();
    userId = recoveryRequest.getUserId();
    deviceId = recoveryRequest.getDeviceId();
    userComment = recoveryRequest.getUserComment();
  }
}
