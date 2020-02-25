package com.craftmaster.lds.fgc.answer;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.craftmaster.lds.fgc.user.User;
import com.craftmaster.lds.fgc.user.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {

  private final AnswerRepository answerRepository;
  private final UserRepository userRepository;

  private final Cache<Long, User> userId2User = Caffeine.newBuilder()
    .expireAfterWrite(15, TimeUnit.MINUTES).build();
  private volatile Map<String, Long> user2Score = new ConcurrentHashMap<>();
  private volatile Map<String, Long> family2Score = new ConcurrentHashMap<>();

  @GetMapping
  public Scores get() {
    return new Scores()
      .setUser2Score(user2Score)
      .setFamily2Score(family2Score);
  }

  @Scheduled(fixedDelay = 60_000)
  public void calculateScores() {
    Map<String, Long> user2Score = new ConcurrentHashMap<>();
    Map<String, Long> family2Score = new ConcurrentHashMap<>();

    answerRepository.findAll().forEach(answer -> {
      if (answer.getQuestion() == null
        || answer.getQuestion().getPointValue() == null
        || answer.getQuestion().getCorrectAnswers() == null
        || answer.getQuestion().getCorrectAnswers().isEmpty()
        || answer.getValues() == null
        || answer.getValues().isEmpty()
      ) {
        return;
      }

      HashSet<String> intersectionOfCorrectAnswers = new HashSet<>(
        answer.getQuestion().getCorrectAnswers());
      intersectionOfCorrectAnswers.retainAll(answer.getValues());

      if (intersectionOfCorrectAnswers.isEmpty()) {
        return;
      }

      User user = userId2User.get(answer.getAnswerPk().getUserId(),
        userId -> userRepository.findById(userId).orElseThrow());

      if (Objects.equals(user.getIsAdmin(), true)) {
        return;
      }

      long pointValue = intersectionOfCorrectAnswers.size() * answer.getQuestion().getPointValue();

      user2Score.put(user.getUsername(),
        user2Score.computeIfAbsent(user.getUsername(), username -> 0L) + pointValue);
      family2Score.put(user.getFamily().getName(),
        family2Score.computeIfAbsent(user.getFamily().getName(), username -> 0L) + pointValue);
    });

    this.user2Score = user2Score;
    this.family2Score = family2Score;
  }
}
