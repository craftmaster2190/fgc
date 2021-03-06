package com.craftmaster.lds.fgc.user;

import static java.util.Objects.requireNonNull;

import com.craftmaster.lds.fgc.config.AwsHeaders;
import com.craftmaster.lds.fgc.config.SessionDeviceId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
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
  private final DeviceInfoRepository deviceInfoRepository;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("generate")
  @Transactional
  public String generateRecoveryCode(@AuthenticationPrincipal User user) {
    return recoveryCodeRepository
        .findByUserId(user.getId())
        .orElseGet(
            () -> {
              recoveryCodeRepository.deleteByUserId(user.getId());
              return recoveryCodeRepository.save(new RecoveryCode().setUserId(user.getId()));
            })
        .getCode();
  }

  @PreAuthorize("!isAuthenticated()")
  @PutMapping
  @Transactional
  public void recoverViaCode(
      @NotBlank @RequestBody RecoveryCodeRequest recoveryCodeRequest, HttpSession httpSession)
      throws NotFoundException {
    String recoveryString = recoveryCodeRequest.getRecoveryCode().toUpperCase();
    RecoveryCode recoveryCodeObject =
        recoveryCodeRepository
            .findById(recoveryString)
            .filter(RecoveryCode::isValid)
            .filter(
                recoveryCode ->
                    userRepository
                        .findById(recoveryCode.getUserId())
                        .filter(
                            user ->
                                user.getName().equals(recoveryCodeRequest.getName())
                                    && user.getFamily()
                                        .getName()
                                        .equals(recoveryCodeRequest.getFamily()))
                        .isPresent())
            .orElseThrow(NotFoundException::new);
    approveRecoverRequest(recoveryCodeObject.getUserId(), SessionDeviceId.get(httpSession));
    recoveryCodeRepository.delete(recoveryCodeObject);
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

    requireNonNull(httpSession, () -> "No session!");
    UUID deviceId = SessionDeviceId.get(httpSession);
    requireNonNull(deviceId, () -> "deviceId was null!");

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
  public Set<RecoveryRequestDetails> adminGetRecoveryRequests() {
    return recoveryRequestRepository.findAll().stream()
        .flatMap(
            recoveryRequest ->
                userRepository.findById(recoveryRequest.getUserId())
                    .map(
                        user ->
                            new RecoveryRequestDetails(recoveryRequest)
                                .setUser(user)
                                .setDeviceInfos(
                                    deviceService
                                        .getOrCreate(recoveryRequest.getDeviceId())
                                        .getDeviceInfos())
                                .setUsersOtherDeviceInfos(
                                    user.getDevices().stream()
                                        .flatMap(device -> device.getDeviceInfos().stream())
                                        .collect(Collectors.toSet())))
                    .stream())
        .collect(Collectors.toSet());
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @PutMapping("request")
  @Transactional
  public void adminApproveRecoveryRequest(@RequestBody LoginUserRequest loginUserRequest)
      throws NotFoundException {
    approveRecoverRequest(loginUserRequest.getUserId(), loginUserRequest.getDeviceId());
  }

  @PreAuthorize("!isAuthenticated()")
  @PatchMapping("request")
  @Transactional
  public void applyUserCommentToRecoveryRequest(
      @RequestBody UserCommentRequest userCommentRequest, HttpSession httpSession) {
    User user =
        userRepository
            .findByNameIgnoreCaseAndFamilyNameIgnoreCase(
                userCommentRequest.getName(), userCommentRequest.getFamily())
            .orElseThrow(NotFoundException::new);

    UUID deviceId = SessionDeviceId.get(httpSession);
    requireNonNull(deviceId, () -> "deviceId was null!");

    RecoveryRequest recoveryRequest =
        recoveryRequestRepository
            .findByUserIdAndDeviceId(user.getId(), deviceId)
            .orElseThrow(NotFoundException::new);

    recoveryRequest.setUserComment(
        (Optional.ofNullable(recoveryRequest.getUserComment()).orElse("")
                + "\n"
                + userCommentRequest.getUserComment())
            .trim());
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
