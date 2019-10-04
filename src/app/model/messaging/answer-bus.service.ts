import { JSONMessageSender } from './json-message-sender';
import { MessageBusService } from './message-bus.service';
import { Answer } from '../answers/answer';
import { AnswersService } from '../answers/answers.service';
import { Question } from '../answers/question';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Message } from '@stomp/stompjs';

@Injectable()
export class AnswerBusService {
  private readonly answerSender: JSONMessageSender;
  private answersCache: { [questionId: number]: Answer } = {};
  private selectedAnswersCache: { [questionId: number]: Set<string> } = {};

  constructor(
    messageBusService: MessageBusService,
    private readonly httpClient: HttpClient
  ) {
    this.answerSender = messageBusService.messageSender("answer");
  }

  getAll() {
    return this.httpClient
      .get<Array<Answer>>("/api/answer/mine")
      .toPromise()
      .then(answers => {
        answers.forEach(answer => {
          this.answersCache[answer.questionId] = answer;
          answer.values.forEach(value =>
            this.getSelectedAnswers(answer.questionId).add(value)
          );
        });
      });
  }

  getSelectedAnswers(questionId: number) {
    return (this.selectedAnswersCache[questionId] =
      this.selectedAnswersCache[questionId] || new Set());
  }

  getAnswer(questionId: number): Answer | undefined {
    return this.answersCache[questionId];
  }

  fetchQuestion(id: number) {
    return this.httpClient.get<Question>(`/api/question/${id}`).toPromise();
  }

  answer(questionId: number, answerText: Array<string>) {
    const answer: Answer = {
      questionId,
      values: answerText
    };

    this.answerSender.convertAndSend(answer);
  }

  updateQuestion(question: Question) {
    this.httpClient.post(`/api/question/`, question).toPromise();
  }

  getPossibleAnswers(id: number) {
    return this.httpClient.get<Array<string>>(`/api/question/possible/${id}`).toPromise();
  }
}
