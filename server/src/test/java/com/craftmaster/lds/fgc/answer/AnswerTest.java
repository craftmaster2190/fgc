package com.craftmaster.lds.fgc.answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.craftmaster.lds.fgc.TestUtil;
import com.craftmaster.lds.fgc.db.ObjectMapperHolderBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnswerTest {

  private Answer answer;

  @Before
  public void before() {
    ObjectMapperHolderBuilder.get().withObjectMapper(new ObjectMapper()).build();
  }

  @After
  public void after() {
    ObjectMapperHolderBuilder.clear();
  }

  @Test
  public void test() {
    TestUtil.testPropertiesExclude(new Answer(), "values");
  }

  @Test
  public void valuesAsAnswer() {
    // we are mostly testing the builder here.
    this.answer = AnswerBuilder.get().withAnswer("joe").withAnswer("wendy").build();
    Set<String> values = answer.getValues();
    assertNotNull("we should have some values", values);
    assertEquals("we should have two values", values.size(), 2);
  }
}
