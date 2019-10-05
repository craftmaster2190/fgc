package com.craftmaster.lds.fgc.user;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("me")
  public User getMe(@AuthenticationPrincipal User user) {
    return user;
  }


  @GetMapping("count")
  public long getAllCount() {
    return userRepository.count();
  }

  @GetMapping("all")
  @RolesAllowed("ROLE_ADMIN")
  public Set<String> getAllUsernames() {
    return StreamSupport.stream(this.userRepository.findAll().spliterator(), false)
      .filter(user -> (!Objects.equals(user.getIsAdmin() , true)))
      .map(User::getUsername)
      .collect(Collectors.toCollection(TreeSet::new));
  }
}
