import { AnswersService } from 'src/app/model/answers/answers.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.sass"]
})
export class GameComponent implements OnInit {
  constructor(public readonly answersService: AnswersService) {}

  openPanels: Record<string, boolean> = {};

  ngOnInit() {
    console.log(this.answersService);
  }
}
