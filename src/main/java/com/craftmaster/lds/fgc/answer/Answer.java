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
import java.util.Set;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
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
  @JoinColumn(name = "familyId")
  @JsonProperty(access = Access.READ_ONLY)
  private Question question;

  @Transient
  private Set<String> values;

  @JsonIgnore
  private String valuesPersisted;

  @PostLoad
  public void postLoad() throws IOException {
    this.setValues(OBJECT_MAPPER.readValue(getValuesPersisted(), new TypeReference<Set<String>>() {
    }));
  }

  @PrePersist
  public void prePersist() throws JsonProcessingException {
    this.setValuesPersisted(OBJECT_MAPPER.writeValueAsString(getValues()));
  }
}


