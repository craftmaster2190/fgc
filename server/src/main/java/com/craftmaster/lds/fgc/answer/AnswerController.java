package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.question.Question;
import com.craftmaster.lds.fgc.question.QuestionService;
import com.craftmaster.lds.fgc.user.User;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnswerController {

  private final AnswerRepository answerRepository;
  private final QuestionService questionService;
  private final SimpMessageSendingOperations simpMessageSendingOperations;

  @MessageMapping("answer")
  @SendToUser("/topic/answer")
  @Transactional
  public void markAnswer(@Valid @Payload Answer answer, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    log.debug("Received answer {} {}", answer, user);
    answer.getAnswerPk().setUserId(user.getId());
    Question question = questionService.getOrCreateById(answer.getAnswerPk().getQuestionId());
    Boolean enabled = question.getEnabled();
    if (Objects.equals(enabled, true)) {
      simpMessageSendingOperations.convertAndSendToUser(
          user.getUsername(), "/topic/answer", answerRepository.save(answer));
    }
    simpMessageSendingOperations.convertAndSendToUser(
        user.getUsername(), "/topic/question", question);
  }

  @SubscribeMapping("answer")
  public List<Answer> getAllMine(Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    return answerRepository.findByAnswerPk_UserId(user.getId());
  }
}
