package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

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
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Set<User> users;

  @ToString.Exclude @JsonIgnore @EqualsAndHashCode.Exclude
  private Instant createdAt = Instant.now();
}
