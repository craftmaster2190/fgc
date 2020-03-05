package com.craftmaster.lds.fgc.question;

import com.craftmaster.lds.fgc.answer.Answer;
import com.craftmaster.lds.fgc.answer.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/question")
@RequiredArgsConstructor
public class QuestionController {

  private final QuestionService questionService;
  private final AnswerRepository answerRepository;
  private final SimpMessageSendingOperations simpMessageSendingOperations;

  @SubscribeMapping("question")
  public Iterable<Question> getAll() {
    return questionService.findAll();
  }

  @PostMapping
  @RolesAllowed("ROLE_ADMIN")
  public Question updateQuestion(@RequestBody Question question) {
    Question savedQuestion = questionService.updateQuestion(question);
    simpMessageSendingOperations.convertAndSend("/topic/question", savedQuestion);
    return savedQuestion;
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

  @PostMapping("disable-all")
  @RolesAllowed("ROLE_ADMIN")
  public void disableAll() {
    getAll().forEach(question -> updateQuestion(question.setEnabled(false)));
  }

  @PostMapping("enabled-all")
  @RolesAllowed("ROLE_ADMIN")
  public void enableAll() {
    getAll().forEach(question -> {
      if (question.getCorrectAnswers() == null || question.getCorrectAnswers().isEmpty()) {
        updateQuestion(question.setEnabled(true));
      }
    });
  }
}
