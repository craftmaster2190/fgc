package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.FamilyRepository;
import com.craftmaster.lds.fgc.user.NotFoundException;
import com.craftmaster.lds.fgc.user.User;
import com.craftmaster.lds.fgc.user.UserRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScoreController {

  private final AnswerRepository answerRepository;
  private final ScoreRepository scoreRepository;
  private final UserRepository userRepository;
  private final FamilyRepository familyRepository;

  private final SimpUserRegistry simpUserRegistry;
  private final SimpMessageSendingOperations simpMessageSendingOperations;
  private final TransactionalContext transactionalContext;

  //  @Scheduled(fixedDelay = 15000L)
  public void sendUsersScores() {
    simpUserRegistry
        .getUsers()
        .forEach(
            user -> {
              var userId = user.getName();
              Score score = transactionalContext.run(() -> get(UUID.fromString(userId)));
              if (score.getScore() > 0) {
                simpMessageSendingOperations.convertAndSendToUser(userId, "/topic/score", score);
              }
            });
  }

  @SubscribeMapping("score")
  public Score listenToScores(Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    return transactionalContext.run(() -> get(user.getId()));
  }

  @Transactional
  public Score get(UUID id) {
    Score score =
        scoreRepository
            .findById(id)
            .orElseGet(
                () ->
                    scoreRepository.save(
                        new Score().setUserOrFamilyId(id).setUpdatedAt(Instant.now())));

    if (score.isValid()) {
      return score;
    }

    return generateUserScore(score)
        .or(() -> generateFamilyScore(score))
        .orElseThrow(() -> new NotFoundException(id + " is neither a user or family."))
        .setUpdatedAt(Instant.now());
  }

  private Optional<Score> generateUserScore(Score userScore) {
    return userRepository
        .findById(userScore.getUserOrFamilyId())
        .map(user -> userScore.setScore(calculateUserScore(userScore.getUserOrFamilyId())));
  }

  private long calculateUserScore(UUID userId) {
    return answerRepository.findByAnswerPk_UserId(userId).stream()
        .mapToLong(Answer::getScore)
        .sum();
  }

  private Optional<Score> generateFamilyScore(Score familyScore) {
    return familyRepository
        .findById(familyScore.getUserOrFamilyId())
        .map(
            family -> {
              var average =
                  family.getUsers().stream()
                      .map(user -> get(user.getId()))
                      .mapToLong(Score::getScore)
                      .filter(value -> value > 0)
                      .average()
                      .orElse(0);
              return familyScore.setScore(Math.round(Math.ceil(average)));
            });
  }
}
