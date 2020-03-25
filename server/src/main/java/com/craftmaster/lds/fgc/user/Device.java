package com.craftmaster.lds.fgc.user;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
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

  @ToString.Exclude @JsonIgnore @EqualsAndHashCode.Exclude
  private Instant createdAt = Instant.now();

  @ToString.Exclude @JsonIgnore @EqualsAndHashCode.Exclude private Instant bannedAt;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "deviceId")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Set<DeviceInfo> deviceInfos;
}
