package com.craftmaster.lds.fgc.chat;

import com.craftmaster.lds.fgc.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatRepository chatRepository;

  @MessageMapping("/chat")
  @SendTo("/topic/chat")
  public Chat sendChat(@NotBlank @Payload String chatString, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    log.debug("Adding chat: {} for: {}", chatString, user);
    Chat chat = chatRepository.save(new Chat().setValue(chatString).setUserId(user.getId()));
    chat.setUser(user);
    return chat;
  }

  @SubscribeMapping("/chat")
  public Iterable<Chat> getAll() {
    return chatRepository.findAll();
  }
}

