package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Data
@Entity
@Accessors(chain = true)
public class Family implements Serializable {
  private static final long serialVersionUID = 20200305L;

  @Id private UUID id = UUID.randomUUID();

  @NotBlank
  @Column(columnDefinition = "text")
  private String name;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "familyId")
  @ToString.Exclude
  @JsonIgnore
  private Set<User> users;

  @Transient
  public Set<String> getUserNames() {
    if (!TransactionSynchronizationManager.isActualTransactionActive()) {
      return Set.of();
    }
    return Optional.ofNullable(getUsers()).stream()
        .flatMap(Collection::stream)
        .map(User::getName)
        .collect(Collectors.toCollection(TreeSet::new));
  }
}
