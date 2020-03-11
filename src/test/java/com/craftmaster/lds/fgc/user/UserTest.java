package com.craftmaster.lds.fgc.user;

import static org.junit.Assert.assertEquals;

import com.craftmaster.lds.fgc.TestUtil;
import java.util.UUID;
import org.junit.Test;

public class UserTest {

  @Test
  public void test() {
    TestUtil.testPropertiesExclude(UserBuilder.get().build(), "familyId", "id");
  }

  @Test
  public void familyId() {
    UUID familyId = UUID.randomUUID();
    assertEquals(familyId, UserBuilder.get().withFamilyId(familyId).build().getFamilyId());
  }

  @Test
  public void id() {
    UUID id = UUID.randomUUID();
    assertEquals(id, UserBuilder.get().withId(id).build().getId());
  }
}
