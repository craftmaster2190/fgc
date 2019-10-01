package com.craftmaster.lds.fgc.user;

import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

  private final UserRepository userRepository;
  private final FamilyRepository familyRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @PostMapping
  public void registerUser(@RequestBody @Valid User user) {
    if (user.getId() != null) {
      throw new UserAlreadyExistsException();
    }
    if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
      throw new UsernameAlreadyTakenException();
    }
    if (user.getFamily().getId() != null) {
      throw new FamilyAlreadyExistsException();
    }

    familyRepository.findByName(user.getFamily().getName())
      .ifPresentOrElse(user::setFamily,
        () -> user.setFamily(familyRepository.save(user.getFamily())));

    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  @GetMapping("family")
  public List<Family> searchFamilies(@RequestParam("search") String partialFamilyName) {
    return familyRepository.findByNameContainingIgnoreCase(partialFamilyName);
  }

  @PostConstruct
  public void generateAdmin() {
    String adminUsername = "ADMIN";
    userRepository.findByUsernameIgnoreCase(adminUsername)
      .ifPresentOrElse(existingAdmin -> {
        log.warn("Admin already exists!");
      }, () -> {
        String adminPassword = UUID.randomUUID().toString();
        registerUser(new User()
          .setUsername(adminUsername)
          .setPassword(adminPassword)
          .setIsAdmin(true)
          .setFamily(new Family().setName("Fisher")));
        log.warn("Created `{}` user with password: `{}`", adminUsername, adminPassword);
      });
  }
}
