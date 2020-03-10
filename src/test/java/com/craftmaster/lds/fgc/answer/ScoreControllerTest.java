package com.craftmaster.lds.fgc.answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.craftmaster.lds.fgc.db.ObjectMapperHolderBuilder;
import com.craftmaster.lds.fgc.question.QuestionBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScoreControllerTest {

	@Mock
	private AnswerRepository answerRepository;

	private ScoreController controller;

	private UUID userId;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		ObjectMapperHolderBuilder.get().withObjectMapper(new ObjectMapper()).build();
		userId = UUID.randomUUID();
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
		when(answerRepository.findByAnswerPk_UserId(userId))
				.thenReturn(AnswerBuilder.asList(AnswerBuilder.get()
						.withQuestion(QuestionBuilder.get()
								.withPointValue(pointValue)
								.withCorrectAnswer("joe")
								.build())
						.withAnswer("joe").build()));
		assertEquals("Our score", pointValue.longValue(), controller.calculateUserScore(userId));
	}
	
	@Test
	public void testWithSomeCorrectAnswer() {
		controller = ScoreControllerBuilder.get()
				.withAnswerRepository(answerRepository)
				.build();
		Long pointValue = 10l;
		when(answerRepository.findByAnswerPk_UserId(userId))
				.thenReturn(AnswerBuilder.asList(AnswerBuilder.get()
						.withQuestion(QuestionBuilder.get()
								.withPointValue(pointValue)
								.withCorrectAnswer("joe")
								.withCorrectAnswer("wendy")
								.build())
						.withAnswer("joe").build()));
		assertEquals("Our score", pointValue.longValue(), controller.calculateUserScore(userId));
	}
	
	@Test
	public void testWithOutCorrectAnswer() {
		controller = ScoreControllerBuilder.get()
				.withAnswerRepository(answerRepository)
				.build();
		Long pointValue = 10l;
		when(answerRepository.findByAnswerPk_UserId(userId))
				.thenReturn(AnswerBuilder.asList(AnswerBuilder.get()
						.withQuestion(QuestionBuilder.get()
								.withPointValue(pointValue)
								.withCorrectAnswer("wendy")
								.build())
						.withAnswer("joe").build()));
		assertEquals("Our score", 0l, controller.calculateUserScore(userId));
	}
	
	@Test
	public void generateFamilyScore() {
		Score familyScore;
		//assertEquals("Family Score", 0l, controller.generateFamilyScore(familyScore))
	}
}
