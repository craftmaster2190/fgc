package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity
@Accessors(chain = true)
public class Family implements Serializable {
  private static final long serialVersionUID = 20200305L;

  @Id
  private UUID id = UUID.randomUUID();

  @NotBlank
  @Column(columnDefinition = "text")
  private String name;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "familyId")
  @ToString.Exclude
  @JsonIgnore
  private Set<User> users;

  @Transient
  public Set<String> getUserNames() {
    return getUsers()
      .stream()
      .map(User::getName)
      .collect(Collectors.toCollection(TreeSet::new));
  }
}
