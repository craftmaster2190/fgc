package com.craftmaster.lds.fgc.user;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Instant> {
  Optional<DeviceInfo> findByDeviceIdAndUserAgentAndInetAddress(
      UUID id, String userAgent, String inetAddress);
}
