package com.craftmaster.lds.fgc.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Not accepting new users at this time!")
public class NotAcceptingNewUsersException extends RuntimeException {
  private static final long serialVersionUID = 20200311L;
}
