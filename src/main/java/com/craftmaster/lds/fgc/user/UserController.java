package com.craftmaster.lds.fgc.user;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("count")
  @Transactional
  public long getAllCount() {
    return userRepository.findByIsAdminIsNullOrIsAdminIsFalse().count();
  }

  @GetMapping("all")
  @Transactional
  @RolesAllowed("ROLE_ADMIN")
  public Set<String> getAllUsernames() {
    log.info("Getting all usernames");
    return userRepository
        .findByIsAdminIsNullOrIsAdminIsFalse()
        .map(
            user ->
                user.getName()
                    + Optional.ofNullable(user.getFamily())
                        .map(Family::getName)
                        .map(familyName -> " (" + familyName + ")")
                        .orElse(""))
        .collect(Collectors.toCollection(TreeSet::new));
  }

  @GetMapping("profile/{userId}")
  public ResponseEntity<Resource> serveFile(@PathVariable UUID userId) {
    return userRepository
        .findById(userId)
        .map(User::getProfileImage)
        .map(ByteArrayResource::new)
        .map(
            resource ->
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .<Resource>body(resource))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("profile")
  public void handleFileUpload(@AuthenticationPrincipal User user, @RequestBody String dataUri)
      throws IOException {
    byte[] imagedata =
        DatatypeConverter.parseBase64Binary(dataUri.substring(dataUri.indexOf(",") + 1));
    userRepository.save(user.setProfileImage(imagedata));
  }
}
