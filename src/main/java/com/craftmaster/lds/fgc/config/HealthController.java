package com.craftmaster.lds.fgc.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("health")
public class HealthController {

  private final Instant startupTime = Instant.now();

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public void ping() {
  }

  @GetMapping("startup")
  public Instant getStartupTime() {
    return startupTime;
  }

}
