package com.craftmaster.lds.fgc.answer;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.craftmaster.lds.fgc.db.ObjectMapperHolderBuilder;
import com.craftmaster.lds.fgc.question.QuestionBuilder;
import com.craftmaster.lds.fgc.user.FamilyBuilder;
import com.craftmaster.lds.fgc.user.FamilyRepository;
import com.craftmaster.lds.fgc.user.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		controller = ScoreControllerBuilder.get()
				.withAnswerRepository(answerRepository)
				.build();
		Long pointValue = 10l;
		when(answerRepository.findByAnswerPk_UserId(userId_a))
				.thenReturn(AnswerBuilder.asList(AnswerBuilder.get()
						.withQuestion(QuestionBuilder.get()
								.withPointValue(pointValue)
								.withCorrectAnswer("joe")
								.build())
						.withAnswer("joe").build()));
		assertEquals("Our score", pointValue.longValue(), controller.calculateUserScore(userId_a));
	}

	@Test
	public void testWithSomeCorrectAnswer() {
		controller = ScoreControllerBuilder.get()
				.withAnswerRepository(answerRepository)
				.build();
		Long pointValue = 10l;
		when(answerRepository.findByAnswerPk_UserId(userId_a))
				.thenReturn(AnswerBuilder.asList(AnswerBuilder.get()
						.withQuestion(QuestionBuilder.get()
								.withPointValue(pointValue)
								.withCorrectAnswer("joe")
								.withCorrectAnswer("wendy")
								.build())
						.withAnswer("joe").build()));
		assertEquals("Our score", pointValue.longValue(), controller.calculateUserScore(userId_a));
	}

	@Test
	public void testWithOutCorrectAnswer() {
		controller = ScoreControllerBuilder.get()
				.withAnswerRepository(answerRepository)
				.build();
		Long pointValue = 10l;
		when(answerRepository.findByAnswerPk_UserId(userId_a))
				.thenReturn(AnswerBuilder.asList(AnswerBuilder.get()
						.withQuestion(QuestionBuilder.get()
								.withPointValue(pointValue)
								.withCorrectAnswer("wendy")
								.build())
						.withAnswer("joe").build()));
		assertEquals("Our score", 0l, controller.calculateUserScore(userId_a));
	}

	@Test
	public void generateFamilyScore() {
		Instant now = Instant.now();
		Score familyScore = ScoreBuilder.get().withScore(10).withUserOrFamilyId(familyId).build();
		controller = ScoreControllerBuilder.get()
				.withFamilyRepository(familyRepository)
				.withScoreRepository(scoreRepository)
				.build();

		when(familyRepository.findById(familyId)).thenReturn(Optional.of(FamilyBuilder.get().withId(familyId)
				.withUser(UserBuilder.get().withId(userId_a).build())
				.withUser(UserBuilder.get().withId(userId_b).build())
				.withUser(UserBuilder.get().withId(userId_c).build())
				.withUser(UserBuilder.get().withId(userId_d).build())
				.build()));
		// the average of 10,20,30 is 20, but in a family of 4, each gets .25% of their
		// score so the weighted average is 15
		when(scoreRepository.findById(userId_a))
				.thenReturn(Optional.of(ScoreBuilder.get().withScore(10).withUpdatedAt(now).build()));
		when(scoreRepository.findById(userId_b))
				.thenReturn(Optional.of(ScoreBuilder.get().withScore(20).withUpdatedAt(now).build()));
		when(scoreRepository.findById(userId_c))
				.thenReturn(Optional.of(ScoreBuilder.get().withScore(30).withUpdatedAt(now).build()));
		// this one is dragging everyone down
		when(scoreRepository.findById(userId_d))
				.thenReturn(Optional.ofNullable(null));

		// deal with any missing scores. Fake out the save on null (userId_d above)
		when(scoreRepository.save(any(Score.class))).thenReturn(ScoreBuilder.get().withScore(0).withUpdatedAt(now).build());

		assertEquals("Family Score", 15l, controller.generateFamilyScore(familyScore).get().getScore());
	}
}
