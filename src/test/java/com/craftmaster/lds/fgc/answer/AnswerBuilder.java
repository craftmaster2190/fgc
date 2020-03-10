package com.craftmaster.lds.fgc.answer;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.craftmaster.lds.fgc.question.Question;

public class AnswerBuilder {

	public static final List<Answer> asList(Answer... answers) {
		List<Answer> list = new ArrayList<>();
		for (Answer answer : answers) {
			list.add(answer);
		}
		return list;
	}

	public static final AnswerBuilder get() {
		return new AnswerBuilder();
	}

	private AnswerBuilder() {
	}

	public Answer build() {
		Answer answer = new Answer();
		answer.setQuestion(question);
		if (answers != null && !answers.isEmpty()) {
			answer.setValuesPersisted(
					format("[%s]", answers.stream().map(e -> format("\"%s\"", e)).collect(Collectors.joining(","))));
		}
		return answer;
	}

	private Question question;

	public AnswerBuilder withQuestion(Question question) {
		this.question = question;
		return this;
	}

	private List<String> answers;

	/**
	 * see withValuesPersisted().
	 * 
	 * @param answer
	 * @return
	 */
	public AnswerBuilder withAnswer(String answer) {
		if (answers == null) {
			answers = new ArrayList<>();
		}
		this.answers.add(answer);
		return this;
	}
}