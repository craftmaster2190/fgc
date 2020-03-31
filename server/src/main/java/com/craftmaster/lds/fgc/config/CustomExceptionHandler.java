package com.craftmaster.lds.fgc.config;

import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler
    implements ApplicationContextAware {

  private volatile ConfigurableApplicationContext applicationContext;

  @ExceptionHandler({
    JpaSystemException.class,
    GenericJDBCException.class,
    DataIntegrityViolationException.class
  })
  public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
    Throwable cause = null;
    while (!ex.getCause().equals(cause) && (cause = ex.getCause()) != null) {
      if (cause instanceof OutOfMemoryError) {
        shutdownContext();
        throw (OutOfMemoryError) cause;
      }
    }
    throw ex;
  }

  public void shutdownContext() {
    applicationContext.close();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = (ConfigurableApplicationContext) applicationContext;
  }
}
