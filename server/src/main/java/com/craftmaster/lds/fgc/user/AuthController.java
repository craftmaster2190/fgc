package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final CustomAuthenticationProvider authenticationManager;
  private final UserRepository userRepository;
  private final DeviceRepository deviceRepository;
  private final FamilyRepository familyRepository;
  private final AccessDeniedExceptionFactory accessDeniedExceptionFactory;
  private final ConfigService configService;
  private final UserService userService;

  @GetMapping("me")
  public ResponseEntity<User> getMe(@AuthenticationPrincipal User user) {
    return Optional.ofNullable(user)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PostMapping
  public User createUser(
      @RequestBody @Valid CreateUserRequest createUserRequest,
      HttpServletRequest request,
      HttpSession session) {
    log.debug("createUser: {}", createUserRequest);
    configService.validateAcceptingNewUsers();
    var user = userService.patchUserInternal(new User(), createUserRequest);
    authenticationManager.authenticate(user, session, request, createUserRequest.getDeviceId());
    return user;
  }

  @PostMapping("login")
  public User loginUser(
      @RequestBody @Valid LoginUserRequest loginUserRequest,
      HttpServletRequest request,
      HttpSession session) {
    log.debug("loginUser: {}", loginUserRequest);
    var user =
        userRepository
            .findById(loginUserRequest.getUserId())
            .orElseThrow(accessDeniedExceptionFactory::get);
    authenticationManager.authenticate(user, session, request, loginUserRequest.getDeviceId());
    return user;
  }

  @PostMapping("logout")
  public void logoutUser(HttpServletRequest request) {
    new SecurityContextLogoutHandler().logout(request, null, null);
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping
  public User patchUser(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid PatchUserRequest patchUserRequest,
      HttpSession session) {
    var savedUser = userService.patchUserInternal(user, patchUserRequest);
    authenticationManager.updateSession(savedUser, session);
    return savedUser;
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @Transactional
  @PutMapping("update-family-name")
  public Family updateFamilyName(@RequestBody UpdateFamilyRequest request) {
    return familyRepository
        .findById(request.getFamilyId())
        .map(
            family -> {
              log.debug(
                  "Updating family name from: {} to: {}", family.getName(), request.getNewName());
              return familyRepository.save(family.setName(request.getNewName()));
            })
        .orElseGet(
            () -> {
              log.warn("No family by id: {}, returning null", request.getFamilyId());
              return null;
            });
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @Transactional
  @PutMapping("update-user-name")
  public User updateUserName(@RequestBody UpdateUserRequest request) {
    return userRepository
        .findById(request.getUserId())
        .map(
            user -> {
              log.debug("Updating user name from: {} to: {}", user.getName(), request.getNewName());
              return userService.patchUserInternal(
                  user, new PatchUserRequest().setName(request.getNewName()));
            })
        .orElseGet(
            () -> {
              log.warn("No user by id: {}, returning null", request.getUserId());
              return null;
            });
  }

  @GetMapping("users")
  public Set<User> getUsersForDevice(
      @RequestParam UUID deviceId, HttpServletRequest request, HttpSession session) {
    SessionDeviceId.set(session, deviceId);
    SessionDeviceFingerprint.set(
        session, request.getHeader(SessionDeviceFingerprint.DEVICE_FINGERPRINT));
    SessionBrowserFingerprint.set(
        session, request.getHeader(SessionBrowserFingerprint.BROWSER_FINGERPRINT));

    return deviceRepository.findById(deviceId).map(Device::getUsers).orElse(Set.of());
  }

  @GetMapping("family")
  public List<Family> searchFamilies(@RequestParam("search") String partialFamilyName) {
    return familyRepository.findByNameContainingIgnoreCase(partialFamilyName);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("families")
  @RolesAllowed("ROLE_ADMIN")
  @Transactional
  public List<FamilyWithUsers> getFamilies() {
    return familyRepository.findAll().stream()
        .map(FamilyWithUsers::new)
        .collect(Collectors.toList());
  }

  // There is no way to become an admin programmatically.
  // This is intentional, the Admin must login to DB and assign their device id.
  @PostConstruct
  public void generateAdmin() {
    String adminName = "Admin";
    userRepository
        .findByNameIgnoreCase(adminName)
        .ifPresentOrElse(
            existingAdmin -> {
              log.warn("{} already exists! ID: {}", adminName, existingAdmin.getId());
            },
            () -> {
              var admin = userRepository.save(new User().setName(adminName).setIsAdmin(true));
              log.warn("Created `{}` user with ID: {}", adminName, admin.getId());
            });
  }
}
