package com.craftmaster.lds.fgc.user;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceService {

  private final DeviceRepository deviceRepository;

  @Transactional
  public Device getOrCreate(UUID deviceId) {
    return deviceRepository
        .findById(deviceId)
        .orElseGet(() -> deviceRepository.save(new Device().setId(deviceId)));
  }

  @Transactional
  public void addDevice(User user, Device device) {
    Optional.ofNullable(user.getDevices())
        .orElseGet(
            () -> {
              HashSet<Device> devices = new HashSet<>();
              user.setDevices(devices);
              return devices;
            })
        .add(device);
  }
}
