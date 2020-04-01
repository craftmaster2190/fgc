import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Answer } from "../answers/answer";
import { JSONMessageSender } from "./json-message-sender";
import { MessageBusService } from "./message-bus.service";
import { Question } from "../question/question";
import { merge } from "rxjs";
import { mapMessageTo } from "../util/map-message-to";
import { tap } from "rxjs/operators";

@Injectable()
export class AnswerBusService {
  private loadedFirstAnswer: boolean;
  private loadedFirstQuestion: boolean;
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

  getLoadedFirstAnswer() {
    return this.loadedFirstAnswer;
  }

  getLoadedFirstQuestion() {
    return this.loadedFirstQuestion;
  }

  listenForQuestionsAndAnswers() {
    return merge(
      this.messageBusService.topicWatcher("answer"),
      this.messageBusService.userTopicWatcher("answer")
    )
      .pipe(
        tap(() => (this.loadedFirstAnswer = true)),
        mapMessageTo<Answer>()
      )
      .subscribe(answers =>
        answers.forEach(answer => {
          this.answersCache[answer.questionId] = answer;
          answer.values.forEach(value =>
            this.getSelectedAnswers(answer.questionId).add(value)
          );
        })
      )
      .add(
        merge(
          this.messageBusService.topicWatcher("question"),
          this.messageBusService.userTopicWatcher("question")
        )
          .pipe(
            tap(() => (this.loadedFirstQuestion = true)),
            mapMessageTo<Question>()
          )
          .subscribe(questions =>
            questions.forEach(question => {
              this.questionsCache[question.id] = question;
            })
          )
      );
  }

  getSelectedAnswers(questionId: number) {
    return (this.selectedAnswersCache[questionId] =
      this.selectedAnswersCache[questionId] || new Set());
  }

  getAnswer(questionId: number): Answer | undefined {
    return this.answersCache[questionId];
  }

  getQuestion(questionId: number): Question {
    return this.questionsCache[questionId] || ({ enabled: true } as any);
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

  disableAll() {
    this.httpClient.post("/api/question/disable-all", null).toPromise();
  }

  enableUnanswered() {
    this.httpClient.post("/api/question/enable-unanswered", null).toPromise();
  }
}
