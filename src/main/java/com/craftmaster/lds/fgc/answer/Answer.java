package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.config.ObjectMapperHolder;
import com.craftmaster.lds.fgc.question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Entity
@Data
@Accessors(chain = true)
public class Answer {


  @JsonUnwrapped
  @EmbeddedId
  private AnswerPk answerPk;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "questionId", insertable = false, updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Question question;
  @JsonIgnore
  private String valuesPersisted;

  @Transient
  public Set<String> getValues() {
    try {
      return ObjectMapperHolder.get().readValue(
        Optional.ofNullable(getValuesPersisted()).orElse("[]"),
        new TypeReference<Set<String>>() {
        });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transient
  public Answer setValues(Set<String> values) {
    try {
      setValuesPersisted(ObjectMapperHolder.get().writeValueAsString(
        Optional.ofNullable(values).orElse(Set.of())));
      return this;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


}


