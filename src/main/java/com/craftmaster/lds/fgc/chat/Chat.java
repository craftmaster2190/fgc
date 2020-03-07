package com.craftmaster.lds.fgc.chat;

import com.craftmaster.lds.fgc.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Chat {

  @JsonIgnore @NotNull private UUID userId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userId", insertable = false, updatable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private User user;

  @NotBlank
  @Column(columnDefinition = "text")
  private String value;

  @Id private Instant id = Instant.now();
}
