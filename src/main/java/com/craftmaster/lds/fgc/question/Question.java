package com.craftmaster.lds.fgc.question;

import com.craftmaster.lds.fgc.config.ObjectMapperHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Question {

  @Id @GeneratedValue @NotNull private Long id;
  @NotNull private Boolean enabled;
  private Long pointValue;

  @JsonIgnore
  @Column(columnDefinition = "text")
  private String correctAnswersPersisted;

  @Transient
  public Set<String> getCorrectAnswers() {
    try {
      return ObjectMapperHolder.get()
          .readValue(
              Optional.ofNullable(getCorrectAnswersPersisted()).orElse("[]"),
              new TypeReference<Set<String>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transient
  public Question setCorrectAnswers(Set<String> values) {
    try {
      setCorrectAnswersPersisted(
          ObjectMapperHolder.get()
              .writeValueAsString(Optional.ofNullable(values).orElse(Set.of())));
      return this;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
