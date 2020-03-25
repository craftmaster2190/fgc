package com.craftmaster.lds.fgc.chat;

import com.craftmaster.lds.fgc.db.PostgresSubscriptions;
import com.craftmaster.lds.fgc.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
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

    postgresSubscriptions.subscribe(
        "DeleteChatId",
        InstantDeserializer.class,
        (id) ->
            simpMessageSendingOperations.convertAndSend(
                "/topic/chat", new Chat().setId(id.toInstant()).setDelete(true)));
  }

  @MessageMapping("/chat")
  public void sendChat(@NotBlank @Payload String chatString, Principal principal)
      throws JsonProcessingException {
    User user = (User) ((Authentication) principal).getPrincipal();
    log.debug("Adding chat: {} for: {}", chatString, user);
    Chat chat = chatRepository.save(new Chat().setValue(chatString).setUserId(user.getId()));
    postgresSubscriptions.send("NewChatId", chat.getId());
  }

  @MessageMapping("/delete-chat")
  @Transactional
  public void deleteChat(@NotNull @Payload InstantDeserializer chatId, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    chatRepository
        .findById(chatId.toInstant())
        .filter(
            chat -> BooleanUtils.isTrue(user.getIsAdmin()) || chat.getUserId().equals(user.getId()))
        .ifPresent(
            chat -> {
              chatRepository.delete(chat);
              postgresSubscriptions.send("DeleteChatId", chatId);
            });
  }

  @SubscribeMapping("/chat")
  public Iterable<Chat> getAll() {
    return chatRepository.findAll();
  }
}
