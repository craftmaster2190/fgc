package com.craftmaster.lds.fgc.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.craftmaster.lds.fgc.TestUtil;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class FamilyTest {

  @Before
  public void before() {
    TransactionSynchronizationManager.setActualTransactionActive(true);
  }

  @After
  public void after() {
    TransactionSynchronizationManager.setActualTransactionActive(false);
  }

  @Test
  public void test() {
    TestUtil.testPropertiesExclude(FamilyBuilder.get().build(), "id");
  }

  @Test
  public void id() {
    UUID id = UUID.randomUUID();
    assertEquals(id, FamilyBuilder.get().withId(id).build().getId());
  }

  @Test
  public void getUserNames() {
    Family family =
        FamilyBuilder.get().withUser(UserBuilder.get().withName("someName").build()).build();
    Set<String> userNames = family.getUserNames();
    assertNotNull("We should have something", userNames);
    assertEquals("We should have one", 1, userNames.size());
  }

  @Test
  public void getUserNamesWithNoUsers() {
    Family family = FamilyBuilder.get().build();
    Set<String> userNames = family.getUserNames();
    assertNotNull("We should have something", userNames);
    assertEquals("We should have none", 0, userNames.size());
  }

  @Test
  public void getUserNamesWithInActiveTransaction() {
    TransactionSynchronizationManager.setActualTransactionActive(false);

    Family family =
        FamilyBuilder.get().withUser(UserBuilder.get().withName("someName").build()).build();
    Set<String> userNames = family.getUserNames();
    assertNotNull("We should have something", userNames);
    assertEquals("We should have none", 0, userNames.size());
  }
}
