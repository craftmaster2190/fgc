package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FamilyWithUsers {
  @JsonUnwrapped private final Family family;
  private final Set<User> users;

  public FamilyWithUsers(Family family) {
    this.family = family;
    // prevent sending back hibernate entity.
    users = Set.copyOf(family.getUsers());
  }
}
