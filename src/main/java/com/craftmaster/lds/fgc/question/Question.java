package com.craftmaster.lds.fgc.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Question {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Id
  @NotNull
  private Long id;
  @NotNull
  private Boolean enabled;

  @JsonIgnore
  @ToString.Exclude
  private String correctAnswersPersisted;

  @Transient
  private Set<String> correctAnswers;

  @PostLoad
  public void postLoad() throws IOException {
    this.setCorrectAnswers(
      OBJECT_MAPPER.readValue(getCorrectAnswersPersisted(), new TypeReference<Set<String>>() {
      }));
  }

  @PrePersist
  public void prePersist() throws JsonProcessingException {
    this.setCorrectAnswersPersisted(OBJECT_MAPPER.writeValueAsString(getCorrectAnswers()));
  }
}
