package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.ConfigService;
import com.craftmaster.lds.fgc.db.PostgresSubscriptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final DeviceRepository deviceRepository;
  private final FamilyRepository familyRepository;
  private final ConfigService configService;
  private final PostgresSubscriptions postgresSubscriptions;

  @Transactional(rollbackOn = {Exception.class, UsernameAlreadyTakenException.class})
  public User patchUserInternal(User user, PatchUserRequest patchUserRequest) {
    log.debug("Patching user: {} name: {} -> {}", user.getId(), user.getName(), patchUserRequest);
    Optional.ofNullable(patchUserRequest.getName())
        .filter(
            (name) -> {
              if (userRepository
                  .findByNameIgnoreCaseAndFamilyNameIgnoreCase(
                      name,
                      Optional.ofNullable(patchUserRequest.getFamily())
                          .or(() -> Optional.ofNullable(user.getFamily()).map(Family::getName))
                          .orElse(null))
                  .isPresent()) {
                throw new UsernameAlreadyTakenException();
              }
              return true;
            })
        .ifPresent(user::setName);
    if (configService.getCanChangeFamily()) {
      if (patchUserRequest.getFamily() != null) {
        user.setFamily(
            familyRepository
                .findByNameIgnoreCase(patchUserRequest.getFamily())
                .orElseGet(
                    () ->
                        familyRepository.save(new Family().setName(patchUserRequest.getFamily()))));
      }
    }

    User savedUser = userRepository.save(user);
    postgresSubscriptions.send("UpdatedUserId", savedUser.getId());
    return savedUser;
  }
}
