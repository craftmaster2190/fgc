package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AccessDeniedExceptionFactory;
import com.craftmaster.lds.fgc.config.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final CustomAuthenticationProvider authenticationManager;
  private final UserRepository userRepository;
  private final DeviceRepository deviceRepository;
  private final FamilyRepository familyRepository;
  private final AccessDeniedExceptionFactory accessDeniedExceptionFactory;

  @PreAuthorize("isAuthenticated()")
  @GetMapping("me")
  public User getMe(@AuthenticationPrincipal User user) {
    log.debug("Asking for me: {}", user);
    return user;
  }

  @PostMapping
  public User createUser(@RequestBody @Valid CreateUserRequest createUserRequest, HttpSession session) {
    log.debug("createUser: {}", createUserRequest);
    var user = userRepository.save(new User());
    authenticationManager.authenticate(user, session , createUserRequest.getDeviceId());
    return user;
  }

  @PostMapping("/login")
  public User loginUser(@RequestBody @Valid LoginUserRequest loginUserRequest, HttpSession session) {
    log.debug("loginUser: {}", loginUserRequest);
    var user = userRepository.findById(loginUserRequest.getUserId()).orElseThrow(accessDeniedExceptionFactory::get);
    authenticationManager.authenticate(user, session , loginUserRequest.getDeviceId());
    return user;
  }

  @PostMapping("/logout")
  public void logoutUser(HttpServletRequest request) {
    new SecurityContextLogoutHandler().logout(request, null, null);
  }

  @PreAuthorize("isAuthenticated()")
  @Transactional
  @PatchMapping
  public User patchUser(@AuthenticationPrincipal User user, @RequestBody @Valid PatchUserRequest patchUserRequest) {
    Optional.ofNullable(patchUserRequest.getName()).filter(StringUtils::hasText).ifPresent(user::setName);
    Optional.ofNullable(patchUserRequest.getFamily()).filter(StringUtils::hasText)
      .flatMap(familyRepository::findByName)
      .ifPresent(user::setFamily);
    return userRepository.save(user);
  }

  @GetMapping("/users")
  public Set<User> getUsersForDevice(@RequestParam UUID deviceId) {
    return deviceRepository.findById(deviceId).map(Device::getUsers).orElse(Set.of());
  }

  @GetMapping("family")
  public List<Family> searchFamilies(@RequestParam("search") String partialFamilyName) {
    return familyRepository.findByNameContainingIgnoreCase(partialFamilyName);
  }

//  @PostConstruct
//  public void generateAdmin() {
//    String adminUsername = "ADMIN";
//    userRepository.findByUsernameIgnoreCase(adminUsername)
//      .ifPresentOrElse(existingAdmin -> {
//        log.warn("Admin already exists!");
//      }, () -> {
//        String adminPassword = "admin";// UUID.randomUUID().toString();
//        registerUser(new User()
//          .setUsername(adminUsername)
//          .setPassword(adminPassword)
//          .setIsAdmin(true)
//          .setFamily(new Family().setName("Fisher")));
//        log.warn("Created `{}` user with password: `{}`", adminUsername, adminPassword);
//      });
//  }
}
