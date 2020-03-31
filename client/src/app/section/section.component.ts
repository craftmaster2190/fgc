import { AnswerBusService } from "src/app/messaging/answer-bus.service";
import {
  Component,
  Input,
  OnInit,
  OnChanges,
  SimpleChanges
} from "@angular/core";
import setIntersect from "../util/set-intersect";
import { Optional } from "../util/optional";

@Component({
  selector: "app-section",
  templateUrl: "./section.component.html",
  styleUrls: ["./section.component.scss"]
})
export class SectionComponent implements OnChanges {
  @Input() title: string;
  @Input() isOpen: boolean;
  @Input() questionIds: Array<number>;
  notes: Array<string> = [];

  constructor(private readonly answersBus: AnswerBusService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if ("isOpen" in changes || "questionIds" in changes) {
      console.log("C", changes, this.questionIds);
      this.questionIds = this.questionIds || [];
      this.notes = [];

      const enabledQuestions = this.questionIds.filter(
        questionId => this.answersBus.getQuestion(questionId)?.enabled
      );

      const totalQuestionsCount = enabledQuestions.length;

      const mySelectAnswersCount = enabledQuestions.filter(
        questionId =>
          (this.answersBus.getSelectedAnswers(questionId)?.size || 0) > 0
      ).length;

      if (
        totalQuestionsCount > 0 &&
        mySelectAnswersCount !== totalQuestionsCount
      ) {
        const remaining = totalQuestionsCount - mySelectAnswersCount;
        const plural = remaining > 1 ? "s" : "";
        this.notes.push(
          `You still have ${remaining} open, unanswered question${plural}`
        );
      }

      this.questionIds.map(questionId => {
        const question = this.answersBus.getQuestion(questionId);
        Optional.of(question?.correctAnswers)
          .map(correctAnswers => new Set<string>(correctAnswers))
          .map(correctAnswersSet => {
            const selectedAnswers = this.answersBus.getSelectedAnswers(
              questionId
            );
            return setIntersect(selectedAnswers, correctAnswersSet);
          })
          .map(scoredSet => scoredSet.size)
          .filter(scoredAnswersCount => !!scoredAnswersCount)
          .map(scoredAnswersCount => {
            const plural = scoredAnswersCount > 1 ? "s" : "";
            this.notes.push(
              `You got ${scoredAnswersCount} answer${plural} correct`
            );
            return scoredAnswersCount * (question?.pointValue || 0);
          })
          .filter(score => !!score)
          .map(score => {
            const plural = score > 1 ? "s" : "";
            this.notes.push(`You have earned ${score} point${plural}`);
          });
      });
    }
  }
}
