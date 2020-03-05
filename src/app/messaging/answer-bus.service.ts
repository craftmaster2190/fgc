import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Answer } from "../answers/answer";
import { JSONMessageSender } from "./json-message-sender";
import { MessageBusService } from "./message-bus.service";
import { Question } from "../question/question";

@Injectable()
export class AnswerBusService {
  private readonly answerSender: JSONMessageSender;

  private questionsCache: { [questionId: number]: Question } = {};
  private answersCache: { [questionId: number]: Answer } = {};
  private selectedAnswersCache: { [questionId: number]: Set<string> } = {};

  constructor(
    private readonly messageBusService: MessageBusService,
    private readonly httpClient: HttpClient
  ) {
    this.answerSender = messageBusService.messageSender("answer");
  }

  listenForQuestionsAndAnswers() {
    return this.messageBusService
      .topicWatcher("answer")
      .subscribe(message => {
        const parsedAnswers = JSON.parse(message.body) as Answer | Answer[];
        const answers = Array.isArray(parsedAnswers)
          ? parsedAnswers
          : [parsedAnswers];

        answers.forEach(answer => {
          this.answersCache[answer.questionId] = answer;
          answer.values.forEach(value =>
            this.getSelectedAnswers(answer.questionId).add(value)
          );
        });
      })
      .add(
        this.messageBusService.topicWatcher("question").subscribe(message => {
          const parsedQuestions = JSON.parse(message.body) as
            | Question
            | Question[];
          const questions = Array.isArray(parsedQuestions)
            ? parsedQuestions
            : [parsedQuestions];

          questions.forEach(question => {
            this.questionsCache[question.id] = question;
          });
        })
      );
  }

  getSelectedAnswers(questionId: number) {
    return (this.selectedAnswersCache[questionId] =
      this.selectedAnswersCache[questionId] || new Set());
  }

  getAnswer(questionId: number): Answer | undefined {
    return this.answersCache[questionId];
  }

  getQuestion(questionId: number) {
    return this.questionsCache[questionId];
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
    return this.httpClient
      .get<Array<string>>(`/api/question/possible/${id}`)
      .toPromise();
  }
}
