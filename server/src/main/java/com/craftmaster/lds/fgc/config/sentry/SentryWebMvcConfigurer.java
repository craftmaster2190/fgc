package com.craftmaster.lds.fgc.config.sentry;

import com.craftmaster.lds.fgc.user.User;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Component
@Conditional(SentryCondition.Enabled.class)
public class SentryWebMvcConfigurer implements WebMvcConfigurer {

  @Override
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.replaceAll(this::wrapHandlerExceptionResolver);
  }

  public HandlerExceptionResolver wrapHandlerExceptionResolver(
      HandlerExceptionResolver handlerExceptionResolver) {
    return (HandlerExceptionResolver)
        Proxy.newProxyInstance(
            handlerExceptionResolver.getClass().getClassLoader(),
            new Class[] {HandlerExceptionResolver.class},
            (object, method, arguments) -> {
              if (method.getName().equals("resolveException")
                  && arguments.length == 4
                  && arguments[3] instanceof Throwable) {
                Throwable ex = (Throwable) arguments[3];
                try {
                  Optional.ofNullable(SecurityContextHolder.getContext())
                      .map(SecurityContext::getAuthentication)
                      .map(
                          authentication -> {
                            Optional.ofNullable(authentication.getCredentials())
                                .ifPresent(
                                    deviceId -> Sentry.getContext().addExtra("deviceId", deviceId));
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
              }

              return method.invoke(handlerExceptionResolver, arguments);
            });
  }
}
