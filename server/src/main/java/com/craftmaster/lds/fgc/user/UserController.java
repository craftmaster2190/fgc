package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AccessDeniedExceptionFactory;
import com.craftmaster.lds.fgc.config.CustomAuthenticationProvider;
import com.craftmaster.lds.fgc.db.PostgresSubscriptions;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
  private final AccessDeniedExceptionFactory accessDeniedExceptionFactory;
  private final CustomAuthenticationProvider authenticationManager;
  private final PostgresSubscriptions postgresSubscriptions;
  private final SimpMessageSendingOperations simpMessageSendingOperations;
  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;

  @PostConstruct
  public void subscribeToNewChats() {
    postgresSubscriptions.subscribe(
        "UpdatedUserId",
        UUID.class,
        (id) ->
            userRepository
                .findById(id)
                .ifPresent(
                    user ->
                        simpMessageSendingOperations.convertAndSend("/topic/updated-user", user)));
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("count")
  @Transactional
  public long getAllCount() {
    return userRepository.findByIsAdminIsNullOrIsAdminIsFalse().count();
  }

  @SubscribeMapping("/updated-user")
  public User getUser(Principal principal) {
    log.info("getUser by principal: {}", principal);
    User user = (User) ((Authentication) principal).getPrincipal();
    return getUser(user.getId());
  }

  @MessageMapping("/get-user")
  @SendToUser("/topic/updated-user")
  public User getUser(@Payload UUID userId) {
    return userRepository.findById(userId).orElse(null);
  }

  @GetMapping("profile/{userId}")
  @Transactional
  public ResponseEntity<Resource> serveFile(
      @PathVariable UUID userId,
      HttpSession session,
      @AuthenticationPrincipal User authenticatedUser) {
    UUID deviceId = (UUID) session.getAttribute("DEVICE_ID");
    if (authenticatedUser == null || deviceId == null) {
      throw accessDeniedExceptionFactory.get();
    }

    return userRepository
        .findById(userId)
        .filter(user -> user.getDevices().stream().map(Device::getId).anyMatch(deviceId::equals))
        .map(User::getUserProfile)
        .map(UserProfile::getProfileImage)
        .map(ByteArrayResource::new)
        .map(
            resource ->
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .<Resource>body(resource))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("profile")
  @Transactional
  public void handleFileUpload(
      @AuthenticationPrincipal User user, @RequestBody String dataUri, HttpSession session)
      throws IOException {
    byte[] imagedata =
        DatatypeConverter.parseBase64Binary(dataUri.substring(dataUri.indexOf(",") + 1));
    userProfileRepository.save(new UserProfile().setId(user.getId()).setProfileImage(imagedata));

    authenticationManager.updateSession(
        userRepository.findById(user.getId()).orElseThrow(), session);

    postgresSubscriptions.send("UpdatedUserId", user.getId());
  }
}
