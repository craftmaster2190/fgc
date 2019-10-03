import { AnswerBusService } from 'src/app/model/messaging/answer-bus.service';
import { AnswersService } from 'src/app/model/answers/answers.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.sass"]
})
export class GameComponent implements OnInit {
  openPanels: { [key: string]: boolean } = {} as any;
  loading: boolean = true;

  constructor(
    public readonly answersService: AnswersService,
    private readonly answerBusService: AnswerBusService
  ) {}

  ngOnInit() {
    this.answerBusService.getAll().then(() => (this.loading = false));
  }
}
