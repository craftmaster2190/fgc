package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.*;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  @Scheduled(fixedDelay = 15000L)
  public void sendUsersFamilyScore() {
    simpUserRegistry
        .getUsers()
        .forEach(
            stompUser ->
                transactionalContext.run(
                    () ->
                        getFamilyScores(UUID.fromString(stompUser.getName()))
                            .ifPresent(
                                scores ->
                                    simpMessageSendingOperations.convertAndSendToUser(
                                        stompUser.getName(), "/topic/family-score", scores))));
  }

  @Transactional
  private Optional<List<Score>> getFamilyScores(UUID userId) {
    return userRepository
        .findById(userId)
        .map(User::getFamily)
        .map(
            family ->
                Stream.concat(
                        Stream.of(get(family.getId())),
                        family.getUsers().stream().map(User::getId).map(this::get))
                    .collect(Collectors.toList()));
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

  @SubscribeMapping("user/family-score")
  public List<Score> listenToFamilyScores(Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();

    return getFamilyScores(user.getId()).orElse(null);
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
    Optional<Family> family = familyRepository.findById(familyScore.getUserOrFamilyId());
    if (family.isEmpty()) {
      return Optional.empty();
    }

    double[] familyScores =
        family.map(Family::getUsers).orElseGet(Set::of).stream()
            .map(user -> get(user.getId()))
            .filter(score -> score.getScore() > 0)
            .mapToDouble(Score::getScore)
            .toArray();

    if (familyScores.length < 3) {
      return Optional.of(familyScore.setScore(0));
    }

    OptionalDouble familyAverage = Arrays.stream(familyScores).average();
    if (familyAverage.isEmpty()) {
      return Optional.of(familyScore.setScore(0));
    }

    var familyPoints = rootMeanSquare(familyScores);

    return Optional.of(familyScore.setScore(Math.round(familyPoints) + familyScores.length));
  }

  public static double rootMeanSquare(double... numbers) {
    var meanSquare = Arrays.stream(numbers).map(num -> Math.pow(num, 2)).sum() / numbers.length;
    return Math.sqrt(meanSquare);
  }
}
