package com.craftmaster.lds.fgc.chat;

import com.craftmaster.lds.fgc.user.User;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

  private final Cache<Instant, Chat> chatStore = Caffeine.newBuilder().maximumSize(100).build();

  @MessageMapping("/chat")
  @SendTo("/topic/chat")
  public Chat markAnswer(@NotBlank @Payload String chatString, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    Chat chat = new Chat().setValue(chatString).setUser(user);
    chatStore.put(Instant.now(), chat);
    return chat;
  }

  @GetMapping
  public Map<Instant, Chat> getAll() {
    return chatStore.asMap();
  }
}

