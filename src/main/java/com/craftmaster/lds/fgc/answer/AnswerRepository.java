package com.craftmaster.lds.fgc.answer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, AnswerPk> {

  List<Answer> findByAnswerPk_UserId(Long id);

  List<Answer> findByAnswerPk_QuestionId(Long id);
}

