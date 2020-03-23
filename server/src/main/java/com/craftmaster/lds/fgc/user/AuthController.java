package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AccessDeniedExceptionFactory;
import com.craftmaster.lds.fgc.config.ConfigService;
import com.craftmaster.lds.fgc.config.CustomAuthenticationProvider;
import com.craftmaster.lds.fgc.db.PostgresSubscriptions;
import java.util.*;
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
  private final PostgresSubscriptions postgresSubscriptions;
  private final ConfigService configService;

  @GetMapping("me")
  public ResponseEntity<User> getMe(@AuthenticationPrincipal User user) {
    return Optional.ofNullable(user)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @Transactional(rollbackOn = {Exception.class, UsernameAlreadyTakenException.class})
  @PostMapping
  public User createUser(
      @RequestBody @Valid CreateUserRequest createUserRequest, HttpSession session) {
    log.debug("createUser: {}", createUserRequest);
    configService.validateAcceptingNewUsers();
    var user = userRepository.save(new User());
    authenticationManager.authenticate(user, session, createUserRequest.getDeviceId());
    return patchUser(user, createUserRequest, session);
  }

  @PostMapping("login")
  public User loginUser(
      @RequestBody @Valid LoginUserRequest loginUserRequest, HttpSession session) {
    log.debug("loginUser: {}", loginUserRequest);
    var user =
        userRepository
            .findById(loginUserRequest.getUserId())
            .orElseThrow(accessDeniedExceptionFactory::get);
    authenticationManager.authenticate(user, session, loginUserRequest.getDeviceId());
    return user;
  }

  @PostMapping("logout")
  public void logoutUser(HttpServletRequest request) {
    new SecurityContextLogoutHandler().logout(request, null, null);
  }

  @PreAuthorize("isAuthenticated()")
  @Transactional(rollbackOn = {Exception.class, UsernameAlreadyTakenException.class})
  @PatchMapping
  public User patchUser(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid PatchUserRequest patchUserRequest,
      HttpSession session) {
    log.info("Patching user: {} name: {}", user.getId(), user.getName());
    Optional.ofNullable(patchUserRequest.getName())
        // .filter((name) -> {
        // if (userRepository.findByNameIgnoreCaseAndFamilyNameIgnoreCase(name).isPresent())
        // {
        // throw new UsernameAlreadyTakenException();
        // }
        // return true;
        // })
        .ifPresent(user::setName);
    if (configService.getCanChangeFamily()) {
      if (patchUserRequest.getFamily() != null) {
        user.setFamily(
            familyRepository
                .findByNameIgnoreCase(patchUserRequest.getFamily())
                .orElseGet(
                    () ->
                        familyRepository.save(new Family().setName(patchUserRequest.getFamily()))));
      }
    }
    User savedUser = userRepository.save(user);
    authenticationManager.updateSession(savedUser, session);

    postgresSubscriptions.send("UpdatedUserId", savedUser.getId());
    return savedUser;
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @Transactional
  @PutMapping("update-family-name")
  public Family updateFamilyName(@RequestBody UpdateFamilyRequest request) {
    Optional<Family> familyOp = familyRepository.findById(request.getFamilyId());
    if (familyOp.isPresent()) {
      log.debug(
          "Updating family name from: {} to: {}", familyOp.get().getName(), request.getNewName());
      Family family = familyOp.get().setName(request.getNewName());
      familyRepository.save(family);
      return family;
    }
    log.warn("No family by id: {}, returning null", request.getFamilyId());
    return null;
  }

  @GetMapping("users")
  public Set<User> getUsersForDevice(@RequestParam UUID deviceId) {
    return deviceRepository.findById(deviceId).map(Device::getUsers).orElse(Set.of());
  }

  @GetMapping("family")
  public List<Family> searchFamilies(@RequestParam("search") String partialFamilyName) {
    return familyRepository.findByNameContainingIgnoreCase(partialFamilyName);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("family-members")
  @Transactional
  public List<String> getFamilyMembers(@AuthenticationPrincipal User user) {
    return Optional.ofNullable(user.getFamilyId()).flatMap(familyRepository::findById)
        .map(Family::getUsers).orElse(Collections.emptySet()).stream()
        .map(User::getName)
        .collect(Collectors.toList());
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
