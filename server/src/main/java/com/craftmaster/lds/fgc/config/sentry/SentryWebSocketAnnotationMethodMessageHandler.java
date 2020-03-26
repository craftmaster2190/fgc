package com.craftmaster.lds.fgc.config.sentry;

import com.craftmaster.lds.fgc.user.Family;
import com.craftmaster.lds.fgc.user.User;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import java.security.Principal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;

@Slf4j
public class SentryWebSocketAnnotationMethodMessageHandler
    extends WebSocketAnnotationMethodMessageHandler {

  public SentryWebSocketAnnotationMethodMessageHandler(
      SubscribableChannel clientInChannel,
      MessageChannel clientOutChannel,
      SimpMessageSendingOperations brokerTemplate) {
    super(clientInChannel, clientOutChannel, brokerTemplate);
    log.info("Loading {}", this.getClass().getSimpleName());
  }

  @Override
  protected void processHandlerMethodException(
      HandlerMethod handlerMethod, Exception exception, Message<?> message) {
    try {
      Principal principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());

      User user = (User) ((Authentication) principal).getPrincipal();
      Sentry.getContext()
          .setUser(
              new UserBuilder().setId(principal.getName()).setUsername(user.getName()).build());
      Optional.ofNullable(user.getFamily())
          .map(Family::getName)
          .ifPresent(familyName -> Sentry.getContext().addExtra("familyName", familyName));

      Sentry.getContext().addExtra("handlerMethod", handlerMethod.toString());
      Sentry.getContext().addExtra("message", message.toString());
    } catch (Exception unexpectedException) {
      Sentry.capture(unexpectedException);
    }
    Sentry.capture(exception);
    super.processHandlerMethodException(handlerMethod, exception, message);
  }
}
