package com.craftmaster.lds.fgc.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteToAngular implements ErrorController {

  @RequestMapping("/error")
  public String handleError() {
    return "/";
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
