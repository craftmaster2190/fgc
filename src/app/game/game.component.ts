import { AnswerBusService } from "src/app/messaging/answer-bus.service";
import { AnswersService } from "src/app/answers/answers.service";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.scss"]
})
export class GameComponent implements OnInit, OnDestroy {
  openPanel: string;
  loading = true;
  subscription: Subscription;

  constructor(
    public readonly answersService: AnswersService,
    private readonly answerBusService: AnswerBusService
  ) {}

  ngOnInit() {
    this.subscription = this.answerBusService.listenForAnswers();
    this.loading = false;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
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
