import { AnswerBusService } from '../messaging/answer-bus.service';
import { AnswersService } from '../answers/answers.service';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { debounceTime, distinctUntilChanged, filter, map } from 'rxjs/operators';
import { merge, Observable, Subject } from 'rxjs';
import { NgbTypeahead, NgbTypeaheadSelectItemEvent } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: "app-question",
  templateUrl: "./question.component.html",
  styleUrls: ["./question.component.sass"]
})
export class QuestionComponent implements OnInit {
  constructor(private readonly answersBus: AnswerBusService) {}
  @Input() text: string;
  @Input() answerType: "typeahead" | "text";
  @Input() answers: Array<string> = [];
  @Input() countOfAnswers: number = 1;
  @Input() id: number;

  value: string;
  selectedAnswers: Set<string>;
  enabled: boolean = true;

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
        ).slice(0, 10);
      })
    );
  };

  updateAnswer = (event?: NgbTypeaheadSelectItemEvent) => {
    const newAnswer = ((event && event.item) || this.value || "").trim();
    if (newAnswer) {
      this.selectedAnswers.add(newAnswer);
    }
    event.preventDefault();
    this.value = "";
    this.persistAnswer();
  };

  removeAnswer = (value: string) => {
    this.selectedAnswers.delete(value);
    this.persistAnswer();
  };

  persistAnswer = () => {
    this.answersBus.answer(this.id, Array.from(this.selectedAnswers));
  };
}
