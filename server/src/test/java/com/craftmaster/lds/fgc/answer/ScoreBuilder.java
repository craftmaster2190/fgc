package com.craftmaster.lds.fgc.answer;

import java.time.Instant;
import java.util.UUID;

public class ScoreBuilder {

  public static final ScoreBuilder get() {
    return new ScoreBuilder();
  }

  private ScoreBuilder() {}

  public Score build() {
    Score score = new Score();
    score.setUserOrFamilyId(userOrFamilyId);
    score.setScore(scoreValue == null ? 0l : scoreValue);
    score.setUpdatedAt(updatedAt);
    return score;
  }

  private UUID userOrFamilyId;

  public ScoreBuilder withUserOrFamilyId(UUID userOrFamilyId) {
    this.userOrFamilyId = userOrFamilyId;
    return this;
  }

  private Long scoreValue;

  public ScoreBuilder withScore(Long score) {
    this.scoreValue = score;
    return this;
  }

  public ScoreBuilder withScore(Integer score) {
    return withScore(Long.valueOf(score));
  }

  private Instant updatedAt;

  public ScoreBuilder withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }
}
