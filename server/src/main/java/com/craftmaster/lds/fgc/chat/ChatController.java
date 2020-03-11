package com.craftmaster.lds.fgc.chat;

import com.craftmaster.lds.fgc.db.PostgresSubscriptions;
import com.craftmaster.lds.fgc.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatRepository chatRepository;
  private final PostgresSubscriptions postgresSubscriptions;
  private final SimpMessageSendingOperations simpMessageSendingOperations;

  @PostConstruct
  public void subscribeToNewChats() {
    postgresSubscriptions.subscribe(
        "NewChatId",
        InstantDeserializer.class,
        (id) ->
            chatRepository
                .findById(id.toInstant())
                .ifPresent(
                    chat -> simpMessageSendingOperations.convertAndSend("/topic/chat", chat)));
  }

  @MessageMapping("/chat")
  public void sendChat(@NotBlank @Payload String chatString, Principal principal)
      throws JsonProcessingException {
    User user = (User) ((Authentication) principal).getPrincipal();
    log.debug("Adding chat: {} for: {}", chatString, user);
    Chat chat = chatRepository.save(new Chat().setValue(chatString).setUserId(user.getId()));
    postgresSubscriptions.send("NewChatId", chat.getId());
  }

  @SubscribeMapping("/chat")
  public Iterable<Chat> getAll() {
    return chatRepository.findAll();
  }
}