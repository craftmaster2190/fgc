package com.craftmaster.lds.fgc.question;

import static java.lang.String.format;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionBuilder {

  public static final QuestionBuilder get() {
    return new QuestionBuilder();
  }

  private QuestionBuilder() {}

  public Question build() {
    Question question = new Question();
    question.setId(id);
    question.setEnabled(enabled);
    question.setPointValue(pointValue);
    question.setLikelyCorrectCount(likelyCorrectCount);
    if (correctAnswers != null && !correctAnswers.isEmpty()) {
      String jsonArrayString =
          format(
              "[%s]",
              correctAnswers.stream()
                  .map(e -> format("\"%s\"", e))
                  .collect(Collectors.joining(",")));
      question.setCorrectAnswersPersisted(jsonArrayString);
    }
    return question;
  }

  private Long id;

  public QuestionBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public QuestionBuilder withId(Integer id) {
    return withId(Long.valueOf(id));
  }

  private Boolean enabled = Boolean.FALSE;

  public QuestionBuilder asEnabled() {
    this.enabled = Boolean.TRUE;
    return this;
  }

  private Long pointValue;

  public QuestionBuilder withPointValue(Long pointValue) {
    this.pointValue = pointValue;
    return this;
  }

  public QuestionBuilder withPointValue(Integer pointValue) {
    return withPointValue(Long.valueOf(pointValue));
  }

  private Long likelyCorrectCount;

  public QuestionBuilder withLikelyCorrectCount(Long likelyCorrectCount) {
    this.likelyCorrectCount = likelyCorrectCount;
    return this;
  }

  public QuestionBuilder withLikelyCorrectCount(Integer likelyCorrectCount) {
    return withLikelyCorrectCount(Long.valueOf(likelyCorrectCount));
  }

  private Set<String> correctAnswers;

  /**
   * This will build the correctAnswersPersisted string.
   *
   * @param correctAnswer
   * @return
   */
  public QuestionBuilder withCorrectAnswer(String correctAnswer) {
    if (correctAnswers == null) {
      correctAnswers = new HashSet<>();
    }
    this.correctAnswers.add(correctAnswer);
    return this;
  }
}
