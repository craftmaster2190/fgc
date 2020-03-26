package com.craftmaster.lds.fgc.config.sentry;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class SentryCondition {
  private static boolean isEnabled(ConditionContext context) {
    String sentryDsn = context.getEnvironment().getProperty("SENTRY_DSN");
    boolean isEnabled = StringUtils.hasText(sentryDsn);
    return isEnabled;
  }

  private static String getClassName(AnnotatedTypeMetadata metadata) {
    return AccessController.doPrivileged(
        (PrivilegedAction<String>)
            () -> {
              Optional<Field> fieldOptional =
                  Optional.ofNullable(ReflectionUtils.findField(metadata.getClass(), "className"));
              fieldOptional.ifPresent(ReflectionUtils::makeAccessible);
              return fieldOptional
                  .map((field) -> (String) ReflectionUtils.getField(field, metadata))
                  .orElse(null);
            });
  }

  public static class Enabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      boolean isEnabled = isEnabled(context);
      log.info(
          "Sentry is {} which means {} is {}",
          isEnabled ? "enabled" : "disabled ",
          getClassName(metadata),
          isEnabled ? "enabled" : "disabled ");
      return isEnabled;
    }
  }

  public static class Disabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      boolean isEnabled = isEnabled(context);
      boolean isDisabled = !isEnabled;
      log.info(
          "Sentry is {} which means {} is {}",
          isEnabled ? "enabled" : "disabled ",
          getClassName(metadata),
          isDisabled ? "enabled" : "disabled ");
      return isDisabled;
    }
  }
}
