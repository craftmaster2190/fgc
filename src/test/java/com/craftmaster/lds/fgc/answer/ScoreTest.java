package com.craftmaster.lds.fgc.answer;

import static org.junit.Assert.assertEquals;

import com.craftmaster.lds.fgc.TestUtil;
import java.time.Instant;
import java.util.UUID;
import org.junit.Test;

public class ScoreTest {

  @Test
  public void test() {
    TestUtil.testPropertiesExclude(new Score(), "userOrFamilyId", "updatedAt");
  }

  @Test
  public void userOrFamilyId() {
    UUID id = UUID.randomUUID();
    Score score = ScoreBuilder.get().withUserOrFamilyId(id).build();
    assertEquals(id, score.getUserOrFamilyId());
  }

  @Test
  public void updatedAt() {
    Instant now = Instant.now();
    Score score = ScoreBuilder.get().withUpdatedAt(now).build();
    assertEquals(now, score.getUpdatedAt());
  }
}
