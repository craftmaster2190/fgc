package com.craftmaster.lds.fgc.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AccessDeniedExceptionFactory {

  public AccessDeniedException get() {
    return new AccessDeniedException("Access is denied");
  }

}
