package com.craftmaster.lds.fgc.user;

import static java.util.Objects.requireNonNull;

import com.craftmaster.lds.fgc.config.AwsHeaders;
import com.craftmaster.lds.fgc.config.SessionDeviceId;
import java.util.*;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/recovery")
@RequiredArgsConstructor
public class RecoveryController {
  private final UserRepository userRepository;
  private final RecoveryCodeRepository recoveryCodeRepository;
  private final RecoveryRequestRepository recoveryRequestRepository;
  private final DeviceService deviceService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("generate")
  @Transactional
  public String generateRecoveryCode(@AuthenticationPrincipal User user) {
    recoveryCodeRepository.deleteByUserId(user.getId());
    return recoveryCodeRepository.save(new RecoveryCode().setUserId(user.getId())).getCode();
  }

  @PreAuthorize("!isAuthenticated()")
  @PutMapping
  @Transactional
  public void recoverViaCode(@RequestBody String recoveryCode, HttpSession httpSession)
      throws NotFoundException {
    RecoveryCode recoveryObject =
        recoveryCodeRepository.findById(recoveryCode).orElseThrow(NotFoundException::new);
    approveRecoverRequest(recoveryObject.getUserId(), SessionDeviceId.get(httpSession));
    recoveryCodeRepository.delete(recoveryObject);
  }

  @PreAuthorize("!isAuthenticated()")
  @PostMapping
  @Transactional
  public ResponseEntity<Void> tryRecoverMe(
      @RequestBody PatchUserRequest patchUserRequest,
      HttpServletRequest httpServletRequest,
      HttpSession httpSession)
      throws NotFoundException {
    String forwardedForAddress = httpServletRequest.getHeader(AwsHeaders.X_FORWARDED_FOR);
    forwardedForAddress = httpServletRequest.getRemoteAddr();
    if (forwardedForAddress == null) {
      throw new RuntimeException("No IP Address!");
    }

    UUID deviceId = SessionDeviceId.get(httpSession);

    // Can recover with IP address?
    User foundUser =
        userRepository
            .findByNameIgnoreCaseAndFamilyNameIgnoreCase(
                patchUserRequest.getName(), patchUserRequest.getFamily())
            .filter(
                user -> user.getDevices().stream().map(Device::getId).noneMatch(deviceId::equals))
            .orElseThrow(NotFoundException::new);
    boolean isCurrentRequesterOnTheSameIpAsAPreviousLogin =
        foundUser.getDevices().stream()
            .flatMap(device -> device.getDeviceInfos().stream())
            .map(DeviceInfo::getInetAddress)
            .anyMatch(forwardedForAddress::equals);

    if (isCurrentRequesterOnTheSameIpAsAPreviousLogin) {
      approveRecoverRequest(foundUser.getId(), deviceId);
      return ResponseEntity.ok().build();
    }

    if (recoveryRequestRepository
        .findByUserIdAndDeviceId(foundUser.getId(), deviceId)
        .isPresent()) {
      return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
    }

    // Otherwise, create a login request
    recoveryRequestRepository.save(
        new RecoveryRequest().setUserId(foundUser.getId()).setDeviceId(deviceId));
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @GetMapping("request")
  @Transactional
  public List<RecoveryRequest> adminGetRecoveryRequests() {
    return recoveryRequestRepository.findAll();
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @PutMapping("request")
  @Transactional
  public void adminApproveRecoveryRequest(@RequestBody LoginUserRequest loginUserRequest)
      throws NotFoundException {
    approveRecoverRequest(loginUserRequest.getUserId(), loginUserRequest.getDeviceId());
  }

  private void approveRecoverRequest(UUID userId, UUID deviceId) throws NotFoundException {
    requireNonNull(userId, "UserId was null");
    requireNonNull(deviceId, "DeviceId was null");
    recoveryRequestRepository
        .findByUserIdAndDeviceId(userId, deviceId)
        .ifPresent(recoveryRequestRepository::delete);
    User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

    Device device = deviceService.getOrCreate(deviceId);
    deviceService.addDevice(user, device);
  }
}
