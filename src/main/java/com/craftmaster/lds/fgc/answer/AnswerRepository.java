package com.craftmaster.lds.fgc.answer;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, AnswerPk> {

  List<Answer> findByAnswerPk_UserId(Long id);
}

