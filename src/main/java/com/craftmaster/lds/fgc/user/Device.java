package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Accessors(chain = true)
public class Device {

  @Id
  private UUID id;

  @ManyToMany(mappedBy = "devices", fetch = FetchType.EAGER)
  @ToString.Exclude
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private Set<User> users;
}
