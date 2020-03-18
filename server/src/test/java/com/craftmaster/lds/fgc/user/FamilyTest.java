package com.craftmaster.lds.fgc.user;

import static org.junit.Assert.*;

import java.util.Set;
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
  public void getUserNames() {
    Family family = new Family().setUsers(Set.of(new User().setName("someName")));
    Set<User> users = family.getUsers();
    assertNotNull("We should have something", users);
    assertEquals("We should have one", 1, users.size());
  }

  @Test
  public void getUserNamesWithNoUsers() {
    Family family = new Family();
    Set<User> users = family.getUsers();
    assertNull("We should have nothing", users);
  }
}
