package com.craftmaster.lds.fgc.answer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Answer {

  @JsonUnwrapped
  @EmbeddedId
  private AnswerPk answerPk;

  @Transient
  private Set<String> values;

  @JsonIgnore
  private String value1, value2, value3, value4, value5;

  @PostLoad
  public void postLoad() {
    this.values = ConcurrentHashMap.newKeySet();
    if (value1 != null) {
      this.values.add(value1);
    }
    if (value2 != null) {
      this.values.add(value2);
    }
    if (value3 != null) {
      this.values.add(value3);
    }
    if (value4 != null) {
      this.values.add(value4);
    }
    if (value5 != null) {
      this.values.add(value5);
    }
  }

  @PrePersist
  public void prePersist() {
    List<String> strings = values == null ? List.of() : List.copyOf(values);
    for (int i = 0; i < strings.size(); i++) {
      switch (i) {
        case 0:
          value1 = strings.get(i);
          break;
        case 1:
          value2 = strings.get(i);
          break;
        case 2:
          value3 = strings.get(i);
          break;
        case 3:
          value4 = strings.get(i);
          break;
        case 4:
          value5 = strings.get(i);
          break;
        default:
          throw new IllegalStateException("There should never be more that 5 values!");
      }
    }
  }
}


