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
import timeout from "../util/timeout";

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
      this.updateNotes();
    }
  }

  updateNotes() {
    if (!this.answersBus.getLoadedFirstQuestion()) {
      timeout(1000).then(() => this.updateNotes());
      return;
    }

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

    let myCorrectAnswers = 0;
    let myCorrectScore = 0;
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
          myCorrectAnswers += scoredAnswersCount;
          return scoredAnswersCount * (question?.pointValue || 0);
        })
        .filter(score => !!score)
        .map(score => {
          myCorrectScore += score;
        });
    });
    if (myCorrectAnswers > 0) {
      const plural = myCorrectAnswers > 1 ? "s" : "";
      this.notes.push(`You got ${myCorrectAnswers} answer${plural} correct`);
    }

    if (myCorrectScore > 0) {
      const plural = myCorrectScore > 1 ? "s" : "";
      this.notes.push(`You have earned ${myCorrectScore} point${plural}`);
    }
  }
}
