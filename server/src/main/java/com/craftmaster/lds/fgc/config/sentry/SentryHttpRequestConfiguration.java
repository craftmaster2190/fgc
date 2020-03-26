package com.craftmaster.lds.fgc.config.sentry;

import com.craftmaster.lds.fgc.user.User;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import io.sentry.spring.SentryExceptionResolver;
import io.sentry.spring.SentryServletContextInitializer;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Configuration
@Conditional(SentryCondition.Enabled.class)
public class SentryHttpRequestConfiguration {
  public SentryHttpRequestConfiguration() {
    log.info("Loading {}", this.getClass().getSimpleName());
  }

  @Bean
  public HandlerExceptionResolver sentryExceptionResolver() {
    return new SentryExceptionResolver() {
      @Override
      public ModelAndView resolveException(
          HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
          Optional.ofNullable(SecurityContextHolder.getContext())
              .map(SecurityContext::getAuthentication)
              .map(
                  authentication -> {
                    Optional.ofNullable(authentication.getCredentials())
                        .ifPresent(deviceId -> Sentry.getContext().addExtra("deviceId", deviceId));
                    return authentication.getPrincipal();
                  })
              .filter(User.class::isInstance)
              .map(User.class::cast)
              .ifPresent(
                  authenticatedUser ->
                      Sentry.getContext()
                          .setUser(
                              new UserBuilder()
                                  .setId(authenticatedUser.getId().toString())
                                  .setUsername(authenticatedUser.getName())
                                  .build()));
        } catch (Exception unexpectedException) {
          Sentry.capture(unexpectedException);
        }
        Sentry.capture(ex);

        // null = run other HandlerExceptionResolvers to actually handle the exception
        return null;
      }
    };
  }

  @Bean
  public ServletContextInitializer sentryServletContextInitializer() {
    return new SentryServletContextInitializer();
  }
}
