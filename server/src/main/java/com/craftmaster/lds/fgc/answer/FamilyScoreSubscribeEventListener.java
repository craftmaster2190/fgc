package com.craftmaster.lds.fgc.answer;

import static java.util.Objects.requireNonNull;

import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FamilyScoreSubscribeEventListener
    implements ApplicationListener<SessionSubscribeEvent> {

  private final ScoreController scoreController;
  private final SimpMessageSendingOperations simpMessageSendingOperations;

  @Override
  public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
    Message<byte[]> message = sessionSubscribeEvent.getMessage();
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
    StompCommand command = headerAccessor.getCommand();
    String destination = headerAccessor.getDestination();

    if (StompCommand.SUBSCRIBE.equals(command) && "/user/topic/family-score".equals(destination)) {
      Principal principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());
      requireNonNull(principal, () -> "No user associated with subscribe event: " + message);

      scoreController
          .getFamilyScores(UUID.fromString(principal.getName()))
          .ifPresent(
              scores ->
                  simpMessageSendingOperations.convertAndSendToUser(
                      principal.getName(), "/topic/family-score", scores));
    }
  }
}
