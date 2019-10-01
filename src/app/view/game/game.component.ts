import { AnswersService } from 'src/app/model/answers/answers.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.sass"]
})
export class GameComponent implements OnInit {
  openPanels: { [key: string]: boolean } = {};

  constructor(public readonly answersService: AnswersService) {}

  ngOnInit() {}
}
