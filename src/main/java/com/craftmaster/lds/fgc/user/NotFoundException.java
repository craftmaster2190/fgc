package com.craftmaster.lds.fgc.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found!")
public class NotFoundException extends RuntimeException {
  private static final long serialVersionUID = 20200305L;
}
