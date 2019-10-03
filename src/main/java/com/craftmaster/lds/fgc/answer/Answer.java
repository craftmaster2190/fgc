package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Answer {


  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
      return OBJECT_MAPPER.readValue(
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
      setValuesPersisted(OBJECT_MAPPER.writeValueAsString(
        Optional.ofNullable(values).orElse(Set.of())));
      return this;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


}


