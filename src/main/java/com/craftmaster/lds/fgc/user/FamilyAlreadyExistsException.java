package com.craftmaster.lds.fgc.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Family already exists!")
public class FamilyAlreadyExistsException extends RuntimeException {
  private static final long serialVersionUID = 20200225L;
}
