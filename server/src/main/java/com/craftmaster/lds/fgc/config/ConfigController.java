package com.craftmaster.lds.fgc.config;

import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/config")
@RestController
@RequiredArgsConstructor
public class ConfigController {

  private final ConfigService configService;

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  @RolesAllowed("ROLE_ADMIN")
  public Map<String, String> getConfigs() {
    return configService.getAll();
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("{key}")
  @RolesAllowed("ROLE_ADMIN")
  public ConfigProperty putConfig(@PathVariable String key, @RequestBody String value) {
    return configService.put(key, value);
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("{key}")
  @RolesAllowed("ROLE_ADMIN")
  @Transactional
  public void deleteConfig(@PathVariable String key) {
    configService.delete(key);
  }

  @SubscribeMapping("server-messages")
  public List<String> getServerMessages() {
    return configService.getServerMessages();
  }

  @PreAuthorize("isAuthenticated()")
  @RolesAllowed("ROLE_ADMIN")
  @PutMapping("can-change-family")
  public boolean toggleFamilyChangeEnable() {
    return configService.toggleCanChangeFamily();
  }

  @GetMapping("can-change-family")
  public boolean getCanChangeFamily() {
    return configService.getCanChangeFamily();
  }
}
