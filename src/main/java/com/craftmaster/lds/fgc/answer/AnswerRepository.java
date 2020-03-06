package com.craftmaster.lds.fgc.answer;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, AnswerPk> {

  List<Answer> findByAnswerPk_UserId(UUID id);

  List<Answer> findByAnswerPk_QuestionId(Long id);
}
