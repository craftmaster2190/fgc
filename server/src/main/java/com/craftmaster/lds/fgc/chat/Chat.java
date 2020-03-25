package com.craftmaster.lds.fgc.chat;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
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

  @NotNull private UUID userId;

  @NotBlank
  @Column(columnDefinition = "text")
  private String value;

  @Id private Instant id = Instant.now();

  @Transient
  @JsonInclude(NON_NULL)
  private Boolean delete;
}
