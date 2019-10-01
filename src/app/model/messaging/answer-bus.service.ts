import { JSONMessageSender } from './json-message-sender';
import { MessageBusService } from './message-bus.service';
import { Answer } from '../answers/answer';
import { AnswersService } from '../answers/answers.service';
import { Injectable } from '@angular/core';
import { Message } from '@stomp/stompjs';

@Injectable()
export class AnswerBusService {
  private readonly answerSender: JSONMessageSender;

  constructor(messageBusService: MessageBusService) {
    this.answerSender = messageBusService.messageSender("answer");
  }

  answer(questionId: number, answerText: Array<string>) {
    const answer: Answer = {
      questionId,
      values: answerText
    };

    this.answerSender.convertAndSend(answer);
  }
}
