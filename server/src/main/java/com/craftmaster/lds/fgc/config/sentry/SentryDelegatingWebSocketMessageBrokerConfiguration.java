package com.craftmaster.lds.fgc.config.sentry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;

@Slf4j
@Configuration
@Conditional(SentryCondition.Enabled.class)
public class SentryDelegatingWebSocketMessageBrokerConfiguration
    extends DelegatingWebSocketMessageBrokerConfiguration {

  public SentryDelegatingWebSocketMessageBrokerConfiguration() {
    log.info("Loading {}", this.getClass().getSimpleName());
  }

  @Override
  protected SimpAnnotationMethodMessageHandler createAnnotationMethodMessageHandler() {
    return new SentryWebSocketAnnotationMethodMessageHandler(
        clientInboundChannel(), clientOutboundChannel(), brokerMessagingTemplate());
  }
}
