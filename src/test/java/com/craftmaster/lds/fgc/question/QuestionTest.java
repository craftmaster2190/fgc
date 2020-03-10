package com.craftmaster.lds.fgc.question;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.craftmaster.lds.fgc.TestUtil;
import com.craftmaster.lds.fgc.db.ObjectMapperHolderBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuestionTest {

	@Before
	public void before() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectMapperHolderBuilder.get().withObjectMapper(objectMapper).build();
	}

	@After
	public void after() {
		ObjectMapperHolderBuilder.clear();
	}

	@Test
	public void test() {
		TestUtil.testProperties(new Question());
	}

	@Test
	public void testWithCorrectedAnswer() {
		Long pointValue = 10l;
		Question question = QuestionBuilder.get()
				.withCorrectAnswer("joe")
				.withPointValue(pointValue)
				.build();
		assertEquals(pointValue, question.getPointValue());
	}

}
