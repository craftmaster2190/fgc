package com.craftmaster.lds.fgc.config;

import com.craftmaster.lds.fgc.config.sentry.SentryCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
public class RouteToAngular {

  @RequestMapping({"/welcome", "/game", "/admin", "/logout"})
  public String handleGameRoute() {
    return "/";
  }
}
