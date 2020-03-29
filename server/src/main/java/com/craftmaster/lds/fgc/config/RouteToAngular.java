package com.craftmaster.lds.fgc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class RouteToAngular {

  @RequestMapping({"/welcome", "/game", "/admin", "/logout"})
  public String handleGameRoute() {
    return "/";
  }
}
