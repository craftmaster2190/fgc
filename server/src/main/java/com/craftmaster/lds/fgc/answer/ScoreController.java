package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.FamilyRepository;
import com.craftmaster.lds.fgc.user.NotFoundException;
import com.craftmaster.lds.fgc.user.User;
import com.craftmaster.lds.fgc.user.UserRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

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

  @Scheduled(fixedDelay = 15000L)
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

  @Scheduled(fixedDelay = 60000L)
  public void sendTop25Scores() {
    userRepository.findAll().forEach(user -> transactionalContext.run(() -> get(user.getId())));
    familyRepository
        .findAll()
        .forEach(family -> transactionalContext.run(() -> get(family.getId())));
    simpMessageSendingOperations.convertAndSend(
        "/topic/score", scoreRepository.findTop25ByOrderByScoreDesc());
  }

  @SubscribeMapping("score")
  public List<Score> listenToScores(Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();

    List<Score> topScores = scoreRepository.findTop25ByOrderByScoreDesc();
    if (topScores.stream().noneMatch(score -> score.getUserOrFamilyId().equals(user.getId()))) {
      topScores.add(transactionalContext.run(() -> get(user.getId())));
    }

    return topScores;
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

  long calculateUserScore(UUID userId) {
    return answerRepository.findByAnswerPk_UserId(userId).stream()
        .mapToLong(Answer::getScore)
        .sum();
  }

  Optional<Score> generateFamilyScore(Score familyScore) {
    return familyRepository
        .findById(familyScore.getUserOrFamilyId())
        .map(
            family -> {
              var familySize = family.getUsers().size();
              var weight = (double) 1 / familySize;
              var average =
                  family.getUsers().stream()
                      .filter(user -> get(user.getId()).getScore() > 0)
                      .mapToDouble(user -> (double) get(user.getId()).getScore() * weight)
                      .sum();
              long score = Math.round(Math.ceil(average));
              return familyScore.setScore(score);
            });
  }
}
