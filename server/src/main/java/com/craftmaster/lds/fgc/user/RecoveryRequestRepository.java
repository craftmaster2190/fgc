package com.craftmaster.lds.fgc.user;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryRequestRepository extends JpaRepository<RecoveryRequest, Instant> {
  Optional<RecoveryRequest> findByUserIdAndDeviceId(UUID userId, UUID deviceId);
}
