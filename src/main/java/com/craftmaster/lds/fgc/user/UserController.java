package com.craftmaster.lds.fgc.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("count")
  public long getAllCount() {
    // ignore admin (total -1)
    return userRepository.count() - 1;
  }

  @GetMapping("all")
  @RolesAllowed("ROLE_ADMIN")
  public Set<String> getAllUsernames() {
    return StreamSupport.stream(this.userRepository.findAll().spliterator(), false)
      .filter(user -> (!Objects.equals(user.getIsAdmin(), true)))
      .map(user -> user.getUsername() + " (" + user.getFamily().getName() + ")")
      .collect(Collectors.toCollection(TreeSet::new));
  }
}
