package com.craftmaster.lds.fgc.answer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, AnswerPk> {

  List<Answer> findByAnswerPk_UserId(UUID id);

  List<Answer> findByAnswerPk_QuestionId(Long id);
}

