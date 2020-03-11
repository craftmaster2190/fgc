import { AnswerBusService } from "src/app/messaging/answer-bus.service";
import { AnswersService } from "src/app/answers/answers.service";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";
import { ImagesCacheService } from "../image/images-cache.service";
import { UserUpdatesService } from "../auth/user-updates.service";
import { DeviceUsersService } from "../auth/device-users.service";

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.scss"]
})
export class GameComponent implements OnInit, OnDestroy {
  openPanel: string = "firstPresidency";
  subscription: Subscription;

  constructor(
    public readonly authService: DeviceUsersService,
    public readonly answersService: AnswersService,
    public readonly answerBusService: AnswerBusService,
    private readonly userUpdates: UserUpdatesService
  ) {}

  ngOnInit() {
    this.subscription = this.answerBusService.listenForQuestionsAndAnswers();
    this.subscription.add(this.userUpdates.startListener());
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
