package com.craftmaster.lds.fgc.question;

import com.craftmaster.lds.fgc.answer.Answer;
import com.craftmaster.lds.fgc.answer.AnswerRepository;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

  private final QuestionService questionService;
  private final AnswerRepository answerRepository;

  @GetMapping("{id}")
  public Question get(@PathVariable long id) {
    return questionService.getOrCreateById(id);
  }

  @PostMapping
  @RolesAllowed("ROLE_ADMIN")
  public Question updateQuestion(@RequestBody Question question) {
   return questionService.updateQuestion(question);
  }

  @GetMapping("possible/{id}")
  @RolesAllowed("ROLE_ADMIN")
  public Set<String> getPossibleAnswers(@PathVariable long id) {
    return answerRepository.findByAnswerPk_QuestionId(id)
      .stream()
      .map(Answer::getValues)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());
  }
}
