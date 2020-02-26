package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.question.Question;
import com.craftmaster.lds.fgc.question.QuestionService;
import com.craftmaster.lds.fgc.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
public class AnswerController {

  private final AnswerRepository answerRepository;
  private final QuestionService questionService;

  @MessageMapping("/answer")
  public void markAnswer(@Valid @Payload Answer answer, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();
    log.debug("Received answer {} {}", answer, user);
    answer.getAnswerPk().setUserId(user.getId());
    Question question = questionService.getOrCreateById(answer.getAnswerPk().getQuestionId());
    Boolean enabled = question
      .getEnabled();
    if (Objects.equals(enabled, true)) {
      answerRepository.save(answer);
    }
  }

  @GetMapping("mine")
  public List<Answer> getAllMine(@AuthenticationPrincipal User user) {
    return answerRepository.findByAnswerPk_UserId(user.getId());
  }
}
