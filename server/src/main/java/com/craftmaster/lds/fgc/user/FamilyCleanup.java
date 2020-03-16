package com.craftmaster.lds.fgc.user;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyCleanup {

  private final FamilyRepository familyRepository;

  @Transactional
  @Scheduled(fixedDelayString = "#{ T(java.util.concurrent.TimeUnit).MINUTES.toMillis(15) }")
  public void cleanupEmptyFamilies() {
    familyRepository.findAll().stream()
        .filter(family -> family.getUsers().isEmpty())
        .forEach(
            family -> {
              log.warn("Cleaning up empty family {}", family);
              familyRepository.delete(family);
            });
  }
}
