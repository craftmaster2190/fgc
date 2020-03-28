package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AwsHeaders;
import com.craftmaster.lds.fgc.config.SessionDeviceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("api/recovery")
@RequiredArgsConstructor
public class RecoveryController {
  private final UserRepository userRepository;
  private final RecoveryCodeRepository recoveryCodeRepository;
  private final RecoveryRequestRepository recoveryRequestRepository;
  private final DeviceService deviceService;

  @Transactional
  public RecoveryCode generateRecoveryCode(@AuthenticationPrincipal User user) {
    return recoveryCodeRepository.save(new RecoveryCode().setUserId(user.getId()));
  }

  @Transactional
  public void codedRecoverMe(@RequestBody String recoveryCode, HttpSession httpSession) throws NotFoundException {
    RecoveryCode recoveryObject = recoveryCodeRepository.findById(recoveryCode).orElseThrow(NotFoundException::new);

    actionRecoverRequest(recoveryObject.getUserId(), SessionDeviceId.get(httpSession));
  }

  @Transactional
  public ResponseEntity<?> checkRecoverMe(
      CreateUserRequest createUserRequest, HttpServletRequest httpServletRequest) throws NotFoundException {
    String forwardedForAddress = httpServletRequest.getHeader(AwsHeaders.X_FORWARDED_FOR);
    if (forwardedForAddress == null) {
      throw new RuntimeException("No IP Address!");
    }
    // Can recover with IP address?
    User foundUser =
        userRepository
            .findByNameIgnoreCaseAndFamilyNameIgnoreCase(
                createUserRequest.getName(), createUserRequest.getFamily())
            .filter(
                user ->
                    user.getDevices().stream()
                        .map(Device::getId)
                        .noneMatch(createUserRequest.getDeviceId()::equals))
            .orElseThrow(NotFoundException::new);
    boolean isCurrentRequesterOnTheSameIpAsAPreviousLogin =
        foundUser.getDevices().stream()
            .flatMap(device -> device.getDeviceInfos().stream())
            .map(DeviceInfo::getInetAddress)
            .anyMatch(forwardedForAddress::equals);

    if (isCurrentRequesterOnTheSameIpAsAPreviousLogin) {
      return ResponseEntity.ok(generateRecoveryCode(foundUser));
    }

    if (recoveryRequestRepository
        .findByUserIdAndDeviceId(foundUser.getId(), createUserRequest.getDeviceId())
        .isPresent()) {
      return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
    }

    // Otherwise, create a login request
    recoveryRequestRepository.save(
        new RecoveryRequest()
            .setUserId(foundUser.getId())
            .setDeviceId(createUserRequest.getDeviceId()));
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @Transactional
  public List<RecoveryRequest> adminGetLoginRequests() {
    return recoveryRequestRepository.findAll();
  }

  @Transactional
  public void adminActionLoginRequest(UUID userId, UUID deviceId) throws NotFoundException {
    actionRecoverRequest(userId, deviceId);
  }

  private void actionRecoverRequest(UUID userId, UUID deviceId) throws NotFoundException {
    recoveryRequestRepository.findByUserIdAndDeviceId(userId, deviceId).ifPresent(recoveryRequestRepository::delete);
    User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

    Device device = deviceService.getOrCreate(deviceId);
    Optional.ofNullable(device.getUsers())
            .orElseGet(() -> {
              HashSet<User> users = new HashSet<>();
              device.setUsers(users);
              return users;
            })
            .add(user);
  }
}

