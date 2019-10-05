import { Answer } from '../answers/answer';
import { AnswerBusService } from '../messaging/answer-bus.service';
import { AnswersService } from '../answers/answers.service';
import { Question } from '../answers/question';
import { AuthService } from 'src/app/view/auth/auth.service';
import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { debounceTime, distinctUntilChanged, filter, map } from 'rxjs/operators';
import { merge, Observable, Subject } from 'rxjs';
import { NgbTypeahead, NgbTypeaheadSelectItemEvent } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: "app-question",
  templateUrl: "./question.component.html",
  styleUrls: ["./question.component.sass"]
})
export class QuestionComponent implements OnInit, OnDestroy {
  interval;
  constructor(
    private readonly answersBus: AnswerBusService,
    private readonly authService: AuthService
  ) {}
  @Input() text: string;
  @Input() answerType: "typeahead" | "text";
  @Input() answers: Array<string> = [];
  @Input() countOfAnswers: number = 1;
  @Input() id: number;

  value: string;
  selectedAnswers: Set<string>;
  question?: Question;
  possibleAnswers: Array<string> = [];
  correctAnswers?: Set<string>;

  @ViewChild("instance", { static: true }) instance: NgbTypeahead;
  focus$ = new Subject<string>();
  click$ = new Subject<string>();

  randomPlaceholder = (() => {
    const placeholders = [
      "Take a guess",
      "What do you think?",
      "Insert guess here",
      "Type something",
      "You can do it",
      "Type a guess",
      "I won't type for you",
      "Press enter when you're done",
      "A guess goes here",
      "Guess correctly for points"
    ];
    return placeholders[Math.floor(Math.random() * placeholders.length)];
  })();

  ngOnInit() {
    this.selectedAnswers = this.answersBus.getSelectedAnswers(this.id);
    const answer = this.answersBus.getAnswer(this.id);
    this.question = answer && this.question;
    if (!this.question) {
      this.fetchQuestion();
    }
    this.interval = setInterval(this.fetchQuestion, 15000);

    if (this.isAdmin()) {
      this.answersBus
        .getPossibleAnswers(this.id)
        .then(possibleAnswers => possibleAnswers.forEach(this.addToPossibleAnswers));
      this.correctAnswers = new Set();
    }
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }

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
        return (term === ""
          ? possibleAnswers
          : possibleAnswers.filter(
              v => v.toLowerCase().indexOf(term.toLowerCase()) > -1
            )
        ).slice(0, 12);
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

  private addToPossibleAnswers = (value) => {
    if (this.possibleAnswers.indexOf(value) === -1) {
      this.possibleAnswers.push(value);
      this.possibleAnswers.sort();
    }
  }

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
    return this.authService.getLoggedInUser().isAdmin;
  };

  isEnabled = () => {
    if (this.isAdmin()) {
      return true;
    }

    if (this.selectedAnswers.size >= this.countOfAnswers) {
      return false;
    }

    if (!this.question) {
      return false;
    }

    return this.question.enabled;
  };

  isQuestionClosed = () => {
    if (!this.question) {
      return false;
    }

    return !this.question.enabled;
  };

  private fetchQuestion = () => {
    this.answersBus.fetchQuestion(this.id).then(question => {
      this.question = question;
      (this.question.correctAnswers || []).sort().forEach(c => {
        this.correctAnswers.add(c);
        this.addToPossibleAnswers(c);
      });
    });
  };

  getPointValue() {
    return this.question && this.question.pointValue;
  }

  updateQuestion() {
    this.answersBus.updateQuestion(this.question);
  }

  toggleCorrectAnswer(answerValue: string) {
    if (this.correctAnswers.has(answerValue)) {
      this.correctAnswers.delete(answerValue);
    } else {
      this.correctAnswers.add(answerValue);
    }
    this.question.correctAnswers = Array.from(this.correctAnswers);
    this.updateQuestion();
  }
}
