package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AccessDeniedExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserRepository userRepository;
  private final FamilyRepository familyRepository;
  private final AccessDeniedExceptionFactory accessDeniedExceptionFactory;

  @GetMapping("me")
  public User getMe(@AuthenticationPrincipal User user) {
    log.debug("Asking for me: {}", user);
    return user;
  }

  @Transactional
  @PostMapping
  public User createUser(@RequestBody @Valid CreateUserRequest createUserRequest, HttpSession session) {
    session.setAttribute("DEVICE_ID", createUserRequest.getDeviceId());
    var user = userRepository.save(new User());
    session.setAttribute("USER_ID", user.getId());
    return user;
  }

  @Transactional
  @PostMapping("/login")
  public User loginUser(@RequestBody @Valid LoginUserRequest loginUserRequest, HttpSession session) {
    session.setAttribute("DEVICE_ID", loginUserRequest.getDeviceId());
    var user = userRepository.findById(loginUserRequest.getUserId()).orElseThrow(accessDeniedExceptionFactory::get);
    session.setAttribute("USER_ID", user.getId());
    return user;
  }


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
  public List<User> getUsersForDevice(@RequestParam UUID deviceId) {
    return userRepository.findDistinctByDevicesId(deviceId);
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
