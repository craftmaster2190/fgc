package com.craftmaster.lds.fgc.question;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

  private final QuestionRepository questionRepository;

  public Iterable<Question> findAll() {
    return questionRepository.findAll();
  }

  @Transactional
  public Question getOrCreateById(long questionId) {
    return questionRepository
        .findById(questionId)
        .orElseGet(
            () -> questionRepository.save(new Question().setId(questionId).setEnabled(true)));
  }

  public Question updateQuestion(Question question) {
    return questionRepository.save(question);
  }
}
