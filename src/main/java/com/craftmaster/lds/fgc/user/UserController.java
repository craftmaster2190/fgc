package com.craftmaster.lds.fgc.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("count")
  @Transactional
  public long getAllCount() {
    return userRepository.findByIsAdminIsNullOrIsAdminIsFalse().count();
  }

  @GetMapping("all")
  @Transactional
  @RolesAllowed("ROLE_ADMIN")
  public Set<String> getAllUsernames() {
    log.info("Getting all usernames");
    return userRepository.findByIsAdminIsNullOrIsAdminIsFalse()
      .map(user -> user.getName()
        + Optional.ofNullable(user.getFamily())
        .map(Family::getName)
        .map(familyName -> " (" + familyName + ")")
        .orElse(""))
      .collect(Collectors.toCollection(TreeSet::new));
  }
}
