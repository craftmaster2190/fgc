package com.craftmaster.lds.fgc.config;

import com.craftmaster.lds.fgc.config.sentry.SentryCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Slf4j
@Conditional(SentryCondition.Disabled.class)
@Configuration
@EnableWebSocketMessageBroker
public class EnableDevWebSocketMessageBroker {
  public EnableDevWebSocketMessageBroker() {
    log.info("Loading {}", this.getClass().getSimpleName());
  }
}
