import { AnswerSender } from './answer-sender';
import { Answer } from '../answers/answer';
import { AnswersService } from '../answers/answers.service';
import { IMessage, Message } from '@stomp/stompjs';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RxStompService } from '@stomp/ng2-stompjs';

@Injectable()
export class AnswerBusService {
  constructor(private readonly rxStompService: RxStompService) {}

  private readonly senders: Record<string, AnswerSender> = {};
  private readonly watchers: Record<string, Observable<IMessage>> = {};

  answer(questionId: number, answerText: Array<string>) {
    const answer: Answer = {
      questionId,
      values: answerText
    };

    this.messageSender("answer").convertAndSend(answer);
  }

  messageSender = (topic: string) => {
    topic = `/app/${topic}`;
    this.senders[topic] =
      this.senders[topic] || new AnswerSender(topic, this.rxStompService);
    return this.senders[topic];
  };

  topicWatcher = (topic: string) => {
    this.watchers[topic] =
      this.watchers[topic] || this.rxStompService.watch(topic);
    return this.watchers[topic];
    // const subscription = messageObservable.subscribe((message: Message) => {
    //   console.log("message recieved", message);
    // });
  };
}
