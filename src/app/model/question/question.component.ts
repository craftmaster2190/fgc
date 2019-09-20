import { AnswersService } from '../answers/answers.service';
import { Component, Input, OnInit } from '@angular/core';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: "app-question",
  templateUrl: "./question.component.html",
  styleUrls: ["./question.component.sass"]
})
export class QuestionComponent implements OnInit {
  private static ID_GENERATOR = 0;

  @Input() text: string;
  @Input() answerType: "typeahead" | "text";
  @Input() answers: Array<string> = [];

  id = "question-" + QuestionComponent.ID_GENERATOR++;
  value: string;

  constructor() {}

  ngOnInit() {}

  search = (text$: Observable<string>) =>
  text$.pipe(
    debounceTime(200),
    distinctUntilChanged(),
    map(term => term.length < 2 ? []
      : this.answers.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
  );
}
