package com.craftmaster.lds.fgc.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @GetMapping("me")
  public User getMe(@AuthenticationPrincipal User user) {
    return user;
  }
}
