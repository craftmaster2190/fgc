package com.craftmaster.lds.fgc.question;

import com.craftmaster.lds.fgc.answer.Answer;
import com.craftmaster.lds.fgc.answer.AnswerRepository;
import com.craftmaster.lds.fgc.db.PostgresSubscriptions;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/question")
@RequiredArgsConstructor
public class QuestionController {

  private final QuestionService questionService;
  private final AnswerRepository answerRepository;
  private final PostgresSubscriptions postgresSubscriptions;
  private final SimpMessageSendingOperations simpMessageSendingOperations;

  @PostConstruct
  public void subscribeToNewChats() {
    postgresSubscriptions.subscribe(
        "UpdatedQuestionId",
        Long.class,
        (id) ->
            simpMessageSendingOperations.convertAndSend(
                "/topic/question", questionService.getOrCreateById(id)));
  }

  @SubscribeMapping("question")
  public Iterable<Question> getAll() {
    return questionService.findAll();
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping
  @RolesAllowed("ROLE_ADMIN")
  public Question updateQuestion(@RequestBody Question question) {
    Question savedQuestion = questionService.updateQuestion(question);
    postgresSubscriptions.send("UpdatedQuestionId", savedQuestion.getId());
    return savedQuestion;
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("possible/{id}")
  @RolesAllowed("ROLE_ADMIN")
  public Set<String> getPossibleAnswers(@PathVariable long id) {
    return answerRepository.findByAnswerPk_QuestionId(id).stream()
        .map(Answer::getValues)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("disable-all")
  @RolesAllowed("ROLE_ADMIN")
  public void disableAll() {
    getAll().forEach(question -> updateQuestion(question.setEnabled(false)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("enable-unanswered")
  @RolesAllowed("ROLE_ADMIN")
  public void enableUnanswered() {
    getAll()
        .forEach(
            question -> {
              if (question.getCorrectAnswers() == null || question.getCorrectAnswers().isEmpty()) {
                updateQuestion(question.setEnabled(true));
              }
            });
  }
}
