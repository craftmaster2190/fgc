package com.craftmaster.lds.fgc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Controller
public class RouteToAngular implements ErrorController {

  @RequestMapping({"/welcome", "/game"})
  public String handleGameRoute() {
    return "/";
  }

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request) {
    log.error(
        "Request {} error: {} (invalidSessionId: {}) {} {} {} [accessed by: {}]",
        request.getAttribute("javax.servlet.error.request_uri"),
        request.getAttribute("javax.servlet.error.status_code"),
        request.getAttribute("org.springframework.session.SessionRepository.invalidSessionId"),
        request.getAttribute("javax.servlet.error.message"),
        request.getAttribute(
            "org.springframework.boot.web.servlet.error.DefaultErrorAttributes.ERROR"),
        request.getAttribute("javax.servlet.error.exception"),
        Optional.of(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getPrincipal));
    return "/";
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
