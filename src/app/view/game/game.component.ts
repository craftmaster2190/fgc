import { AnswerBusService } from "src/app/model/messaging/answer-bus.service";
import { AnswersService } from "src/app/model/answers/answers.service";
import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.scss"]
})
export class GameComponent implements OnInit {
  openPanel: string;
  loading = true;

  constructor(
    public readonly answersService: AnswersService,
    private readonly answerBusService: AnswerBusService
  ) {}

  ngOnInit() {
    this.answerBusService.getAll().then(() => (this.loading = false));
  }

  toggelOpenPanel($event, name: string) {
    if (this.openPanel === name) {
      this.openPanel = null;
    } else {
      this.openPanel = name;
      setTimeout(() => {
        $event.target.scrollIntoView({ behavior: "smooth" });
      }, 100);
    }
  }
}
