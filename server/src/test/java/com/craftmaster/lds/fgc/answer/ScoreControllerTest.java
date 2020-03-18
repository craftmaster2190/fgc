package com.craftmaster.lds.fgc.answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.craftmaster.lds.fgc.db.ObjectMapperHolderBuilder;
import com.craftmaster.lds.fgc.question.QuestionBuilder;
import com.craftmaster.lds.fgc.user.Family;
import com.craftmaster.lds.fgc.user.FamilyRepository;
import com.craftmaster.lds.fgc.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScoreControllerTest {

  @Mock private AnswerRepository answerRepository;
  @Mock private FamilyRepository familyRepository;
  @Mock private ScoreRepository scoreRepository;

  private ScoreController controller;

  private UUID userId_a;
  private UUID userId_b;
  private UUID userId_c;
  private UUID userId_d;
  private UUID familyId;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    ObjectMapperHolderBuilder.get().withObjectMapper(new ObjectMapper()).build();
    userId_a = UUID.fromString("aaaaaaaa-3ab9-4c54-bf91-c450007a8c58");
    userId_b = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    userId_c = UUID.fromString("cccccccc-3ab9-4c54-bf91-c450007a8c58");
    userId_d = UUID.fromString("dddddddd-3ab9-4c54-bf91-c450007a8c58");
    familyId = UUID.fromString("ffffffff-3ab9-4c54-bf91-c450007a8c58");
  }

  @After
  public void after() {
    ObjectMapperHolderBuilder.clear();
  }

  @Test
  public void testWithCorrectAnswer() {
    controller = ScoreControllerBuilder.get().withAnswerRepository(answerRepository).build();
    Long pointValue = 10L;
    when(answerRepository.findByAnswerPk_UserId(userId_a))
        .thenReturn(
            AnswerBuilder.asList(
                AnswerBuilder.get()
                    .withQuestion(
                        QuestionBuilder.get()
                            .withPointValue(pointValue)
                            .withCorrectAnswer("joe")
                            .build())
                    .withAnswer("joe")
                    .build()));
    assertEquals("Our score", pointValue.longValue(), controller.calculateUserScore(userId_a));
  }

  @Test
  public void testWithSomeCorrectAnswer() {
    controller = ScoreControllerBuilder.get().withAnswerRepository(answerRepository).build();
    Long pointValue = 10L;
    when(answerRepository.findByAnswerPk_UserId(userId_a))
        .thenReturn(
            AnswerBuilder.asList(
                AnswerBuilder.get()
                    .withQuestion(
                        QuestionBuilder.get()
                            .withPointValue(pointValue)
                            .withCorrectAnswer("joe")
                            .withCorrectAnswer("wendy")
                            .build())
                    .withAnswer("joe")
                    .build()));
    assertEquals("Our score", pointValue.longValue(), controller.calculateUserScore(userId_a));
  }

  @Test
  public void testWithOutCorrectAnswer() {
    controller = ScoreControllerBuilder.get().withAnswerRepository(answerRepository).build();
    Long pointValue = 10L;
    when(answerRepository.findByAnswerPk_UserId(userId_a))
        .thenReturn(
            AnswerBuilder.asList(
                AnswerBuilder.get()
                    .withQuestion(
                        QuestionBuilder.get()
                            .withPointValue(pointValue)
                            .withCorrectAnswer("wendy")
                            .build())
                    .withAnswer("joe")
                    .build()));
    assertEquals("Our score", 0L, controller.calculateUserScore(userId_a));
  }

  @Test
  public void generateFamilyScore1() {
    assertEquals("Starting team", 25L, testFamilyScores(0, 10, 20, 30));
    assertEquals("Lowest scoring player leaves team", 25L, testFamilyScores(10, 20, 30));
    assertEquals(
        "Lowest scoring player leaves team (must have at least 3 players)",
        0L,
        testFamilyScores(20, 30));

    assertEquals("Lower scoring player joins team", 24L, testFamilyScores(5, 20, 30));
    assertEquals("Higher scoring player joins team", 27L, testFamilyScores(20, 20, 30));
  }

  @Test
  public void generateFamilyScore2() {
    assertEquals("Starting team", 30L, testFamilyScores(0, 10, 20, 30, 25, 35));
    assertEquals("Lowest scoring player leaves team", 30L, testFamilyScores(10, 20, 30, 25, 35));
    assertEquals("Higher scoring player joins team", 34L, testFamilyScores(10, 20, 30, 25, 35, 40));
    assertEquals("Lower scoring player gains points", 35, testFamilyScores(15, 20, 30, 25, 35, 40));
    assertEquals(
        "Higher scoring player gains points", 36L, testFamilyScores(10, 20, 30, 25, 35, 45));
    assertEquals("All player gains points", 40, testFamilyScores(15, 25, 35, 30, 40, 50));

    assertEquals(
        "Lowest scoring player leaves team 5 remaining", 42, testFamilyScores(25, 35, 30, 40, 50));
    assertEquals(
        "Lowest scoring player leaves team 4 remaining", 43, testFamilyScores(35, 30, 40, 50));
    assertEquals("Lowest scoring player leaves team 3 remaining", 45, testFamilyScores(35, 40, 50));
  }

  @Test
  public void generateFamilyScore3() {
    assertEquals("Team of 3 players", 43L, testFamilyScores(40, 40, 40));
    assertEquals("Team of 4 players", 44L, testFamilyScores(40, 40, 40, 40));
    assertEquals("Team of 5 players", 45L, testFamilyScores(40, 40, 40, 40, 40));
    assertEquals("Team of 6 players", 46L, testFamilyScores(40, 40, 40, 40, 40, 40));
    assertEquals("Team of 7 players", 47L, testFamilyScores(40, 40, 40, 40, 40, 40, 40));
    assertEquals("Team of 8 players", 48L, testFamilyScores(40, 40, 40, 40, 40, 40, 40, 40));
    assertEquals("Team of 9 players", 49L, testFamilyScores(40, 40, 40, 40, 40, 40, 40, 40, 40));
    assertEquals(
        "Team of 10 players", 50L, testFamilyScores(40, 40, 40, 40, 40, 40, 40, 40, 40, 40));
  }

  private long testFamilyScores(long... scores) {
    controller =
        ScoreControllerBuilder.get()
            .withFamilyRepository(familyRepository)
            .withScoreRepository(scoreRepository)
            .build();

    Instant now = Instant.now();
    Score familyScore = ScoreBuilder.get().withScore(10).withUserOrFamilyId(familyId).build();

    Family family = new Family().setId(familyId);
    Set<User> users = new HashSet<>();

    for (long score : scores) {
      UUID userId = UUID.randomUUID();
      users.add(new User().setId(userId));
      when(scoreRepository.findById(userId))
          .thenReturn(Optional.of(new Score().setScore(score).setUpdatedAt(now)));
    }

    when(familyRepository.findById(familyId)).thenReturn(Optional.of(family.setUsers(users)));
    when(scoreRepository.save(any(Score.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    return controller.generateFamilyScore(familyScore).orElseThrow().getScore();
  }
}
