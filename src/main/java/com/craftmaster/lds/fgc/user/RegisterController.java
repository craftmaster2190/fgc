package com.craftmaster.lds.fgc.user;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RegisterController {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @PostMapping
  public void registerAdmin(@RequestBody @Valid User user) {
    if (user.getId() != null) {
      throw new UserAlreadyExistsException();
    }
    if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
      throw new UsernameAlreadyTakenException();
    }
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  @PostMapping("/player")
  public void registerPlayer(@RequestBody Player player) {
	  log.info("body: {}", player);
  }
  
  @PostConstruct
  public void generateAdmin() {
    String adminUsername = "ADMIN";
    userRepository.findByUsernameIgnoreCase(adminUsername)
      .ifPresentOrElse(existingAdmin -> {
        log.warn("Admin already exists!");
      }, () -> {
        String adminPassword = UUID.randomUUID().toString();
        registerAdmin(new User()
          .setUsername(adminUsername)
          .setPassword(adminPassword)
          .setIsAdmin(true));
        log.warn("Created `{}` user with password: `{}`", adminUsername, adminPassword);
      });
  }
}
