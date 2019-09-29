package com.craftmaster.lds.fgc.answer;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, Long> {

  List<Answer> findByAnswerPk_UserId(Long id);
}

