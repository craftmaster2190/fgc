package com.craftmaster.lds.fgc.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

public class EnableSentryCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String sentryDsn = context.getEnvironment().getProperty("SENTRY_DSN");
    return StringUtils.hasText(sentryDsn);
  }
}
