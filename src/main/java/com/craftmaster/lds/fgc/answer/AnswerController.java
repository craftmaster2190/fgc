package com.craftmaster.lds.fgc.answer;

import com.craftmaster.lds.fgc.user.User;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/api/answer")
@RequiredArgsConstructor
public class AnswerController {

  private final AnswerRepository answerRepository;

  @MessageMapping("/answer")
  public void markAnswer(@Valid @Payload Answer answer, @AuthenticationPrincipal User user) {
    log.debug("Received answer {} {}", answer, user);
    answer.getAnswerPk().setUserId(user.getId());
    answerRepository.save(answer);
  }

  @GetMapping("mine")
  public List<Answer> getAllMine( @AuthenticationPrincipal User user) {
    return answerRepository.findByAnswerPk_UserId(user.getId());
  }
}
