package com.craftmaster.lds.fgc.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.craftmaster.lds.fgc.TestUtil;
import java.util.Collection;
import java.util.UUID;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

public class UserTest {

  @Test
  public void test() {
    TestUtil.testPropertiesExclude(UserBuilder.get().build(), "familyId", "id", "createdAt");
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

  @Test
  public void getUsername() {
    UUID id = UUID.randomUUID();
    assertEquals(id.toString(), UserBuilder.get().withId(id).build().getUsername());
  }

  @Test
  public void getAuthorities() {
    Collection<? extends GrantedAuthority> authorities = UserBuilder.get().build().getAuthorities();
    assertNotNull("We should always get something", authorities);
    assertEquals("We should have none", 0, authorities.size());
  }

  @Test
  public void getAuthoritiesAsAdmin() {
    Collection<? extends GrantedAuthority> authorities =
        UserBuilder.get().asAdmin().build().getAuthorities();
    assertNotNull("We should always get something", authorities);
    assertEquals("We should have one", 1, authorities.size());
  }
}
