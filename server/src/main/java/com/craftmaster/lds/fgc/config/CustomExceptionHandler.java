package com.craftmaster.lds.fgc.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(JpaSystemException.class)
  public final ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
    Throwable cause = null;
    while (!ex.getCause().equals(cause) && (cause = ex.getCause()) != null) {
      if (cause instanceof OutOfMemoryError) {
        throw (OutOfMemoryError) cause;
      }
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
