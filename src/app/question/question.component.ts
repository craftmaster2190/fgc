import { Component, Input, OnDestroy, OnInit, ViewChild } from "@angular/core";
import {
  NgbTypeahead,
  NgbTypeaheadSelectItemEvent
} from "@ng-bootstrap/ng-bootstrap";
import { merge, Observable, Subject } from "rxjs";
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  map
} from "rxjs/operators";
import { DeviceUsersService } from "src/app/auth/device-users.service";
import { AnswerBusService } from "../messaging/answer-bus.service";
import { Question } from "./question";
import { ImagesService } from "../answers/images.service";

@Component({
  selector: "app-question",
  templateUrl: "./question.component.html",
  styleUrls: ["./question.component.scss"]
})
export class QuestionComponent implements OnInit, OnDestroy {
  constructor(
    private readonly answersBus: AnswerBusService,
    private readonly authService: DeviceUsersService,
    public readonly images: ImagesService
  ) {}
  @Input() text: string;
  @Input() answerType: "typeahead" | "text";
  @Input() answers: Array<string> = [];
  @Input() countOfAnswers: number = 1;
  @Input() id: number;
  @Input() isColors: boolean = false;
  @Input() image: string;
  imageLink: string;

  value: string;
  selectedAnswers: Set<string>;
  possibleAnswers: Array<string> = [];
  correctAnswers?: Set<string>;

  @ViewChild("instance", { static: true }) instance: NgbTypeahead;
  focus$ = new Subject<string>();
  click$ = new Subject<string>();

  randomPlaceholder = (() => {
    const placeholders = [
      "Take a guess",
      "Take a wild guess",
      "What do you think?",
      "Insert guess here",
      "Type something",
      "You can do it",
      "Type a guess",
      "I won't type for you",
      "Press enter when you're done",
      "A guess goes here",
      "Guess correctly for points",
      "Type here",
      "Don't leave me blank!"
    ];
    return placeholders[Math.floor(Math.random() * placeholders.length)];
  })();

  ngOnInit() {
    this.selectedAnswers = this.answersBus.getSelectedAnswers(this.id);

    if (this.isAdmin()) {
      this.answersBus
        .getPossibleAnswers(this.id)
        .then(possibleAnswers =>
          possibleAnswers.forEach(this.addToPossibleAnswers)
        );
      this.correctAnswers = new Set();
    }

    if (this.image) {
      this.imageLink = this.images.getPerson(this.image);
    }
  }

  ngOnDestroy() {}

  search = (text$: Observable<string>) => {
    const debouncedText$ = text$.pipe(
      debounceTime(200),
      distinctUntilChanged()
    );
    const clicksWithClosedPopup$ = this.click$.pipe(
      filter(() => !this.instance.isPopupOpen())
    );
    const inputFocus$ = this.focus$;

    return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
      map(term => {
        const possibleAnswers = this.answers.filter(
          v => !this.selectedAnswers.has(v)
        );
        const filteredAnswers =
          term === ""
            ? possibleAnswers
            : possibleAnswers.filter(
                v => v.toLowerCase().indexOf(term.toLowerCase()) > -1
              );
        return this.isColors ? filteredAnswers : filteredAnswers.slice(0, 12);
      })
    );
  };

  updateAnswer = (event?: NgbTypeaheadSelectItemEvent) => {
    if (!event && this.instance.isPopupOpen()) {
      return;
    }

    if (this.isEnabled()) {
      const newAnswer = ((event && event.item) || this.value || "").trim();
      if (newAnswer) {
        this.selectedAnswers.add(newAnswer);

        if (this.isAdmin()) {
          this.addToPossibleAnswers(newAnswer);
        }
      }
      event && event.preventDefault();
      this.value = "";
      this.persistAnswer();
    }
  };

  private addToPossibleAnswers = value => {
    if (this.possibleAnswers.indexOf(value) === -1) {
      this.possibleAnswers.push(value);
      this.possibleAnswers.sort();
    }
  };

  removeAnswer = (value: string) => {
    if (!this.isQuestionClosed()) {
      this.selectedAnswers.delete(value);
      this.persistAnswer();
    }
  };

  persistAnswer = () => {
    this.answersBus.answer(this.id, Array.from(this.selectedAnswers));
  };

  isAdmin = () => {
    return this.authService.getCurrentUser()?.isAdmin;
  };

  isEnabled = () => {
    if (this.isAdmin()) {
      return true;
    }

    if (this.selectedAnswers.size >= this.countOfAnswers) {
      return false;
    }

    const question = this.getQuestion();
    if (!question) {
      return false;
    }

    return question.enabled;
  };

  isQuestionClosed = () => {
    const question = this.getQuestion();
    if (!question) {
      return false;
    }

    return !question.enabled;
  };

  getQuestion = () => {
    const question = this.answersBus.getQuestion(this.id);
    (question.correctAnswers || []).sort().forEach(c => {
      this.correctAnswers.add(c);
      this.addToPossibleAnswers(c);
    });
    return question;
  };

  getPointValue() {
    return this.getQuestion()?.pointValue;
  }

  updateQuestion() {
    this.answersBus.updateQuestion(this.getQuestion());
  }

  toggleCorrectAnswer(answerValue: string) {
    if (this.correctAnswers.has(answerValue)) {
      this.correctAnswers.delete(answerValue);
    } else {
      this.correctAnswers.add(answerValue);
    }
    this.getQuestion().correctAnswers = Array.from(this.correctAnswers);
    this.updateQuestion();
  }

  getAverageValue() {
    const score = this.getQuestion()?.pointValue || 1;
    const guesses = this.countOfAnswers;
    const likely =
      this.getQuestion()?.likelyCorrectCount || this.countOfAnswers;
    const possible = this.answers?.length;
    if (!possible) {
      return 0;
    }

    let numberCorrect = 1;

    const weightedArray = Array(guesses);
    while (numberCorrect <= guesses) {
      const valueCorrect = numberCorrect * score;
      let value = 1;
      let top = likely;
      let bottom = possible;
      for (let n = numberCorrect; n > 0; n--) {
        value *= top-- / bottom--;
      }
      weightedArray[numberCorrect++] = value * valueCorrect;
    }

    const weightedAverageScore = weightedArray.reduce((a, b) => a + b, 0);
    return weightedAverageScore.toFixed(1);
  }

  sumOfNMinus1OverMMinus1LimitNGreaterThan0(top: number, bottom: number) {
    if (top >= bottom) {
      throw new Error("Um....");
    }

    let value = 1;

    while (top > 0) {
      value *= top-- / bottom--;
    }

    return value;
  }
}
