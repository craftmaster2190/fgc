package com.craftmaster.lds.fgc.user;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class Device implements Serializable {
  private static final long serialVersionUID = 20200305L;

  @JsonProperty(access = WRITE_ONLY)
  @Id
  private UUID id = UUID.randomUUID();

  @ManyToMany(mappedBy = "devices", fetch = FetchType.EAGER)
  @ToString.Exclude
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private Set<User> users;
}
