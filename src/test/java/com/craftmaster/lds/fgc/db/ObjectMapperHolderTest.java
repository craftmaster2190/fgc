package com.craftmaster.lds.fgc.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

// till we get to junit 5, this is the way to control order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ObjectMapperHolderTest {

  @After
  public void after() {
    ObjectMapperHolderBuilder.clear();
  }

  @Test
  public void testA() {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectMapperHolderBuilder.get().withObjectMapper(objectMapper).build();
    assertNotNull(ObjectMapperHolder.get());
  }

  @Test
  public void testB() {
    // the after should have cleared the variable
    assertNull(ObjectMapperHolder.get());
  }
}
