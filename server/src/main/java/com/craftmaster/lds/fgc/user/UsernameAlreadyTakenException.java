package com.craftmaster.lds.fgc.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Username already taken!")
public class UsernameAlreadyTakenException extends RuntimeException {
  private static final long serialVersionUID = 20190930L;
}
