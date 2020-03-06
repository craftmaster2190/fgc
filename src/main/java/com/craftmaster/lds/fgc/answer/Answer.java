package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.config.ObjectMapperHolder;
import com.craftmaster.lds.fgc.question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Answer {

  @JsonUnwrapped @EmbeddedId private AnswerPk answerPk;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "questionId", insertable = false, updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Question question;

  @JsonIgnore
  @Column(columnDefinition = "text")
  private String valuesPersisted;

  @Transient
  public Set<String> getValues() {
    try {
      return ObjectMapperHolder.get()
          .readValue(
              Optional.ofNullable(getValuesPersisted()).orElse("[]"),
              new TypeReference<Set<String>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transient
  public Answer setValues(Set<String> values) {
    try {
      setValuesPersisted(
          ObjectMapperHolder.get()
              .writeValueAsString(Optional.ofNullable(values).orElse(Set.of())));
      return this;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @JsonIgnore
  @Transient
  public boolean isScorable() {
    return getQuestion() != null
        && getQuestion().getPointValue() != null
        && getQuestion().getCorrectAnswers() != null
        && !getQuestion().getCorrectAnswers().isEmpty()
        && getValues() != null
        && !getValues().isEmpty();
  }

  @Transient
  public long getScore() {
    if (isScorable()) {
      HashSet<String> intersection = new HashSet<>(getValues());
      intersection.retainAll(getQuestion().getCorrectAnswers());
      return intersection.size() * getQuestion().getPointValue();
    }
    return 0;
  }
}
