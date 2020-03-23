import { AnswerBusService } from "src/app/messaging/answer-bus.service";
import { AnswersService } from "src/app/answers/answers.service";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";
import { ImagesCacheService } from "../image/images-cache.service";
import { UserUpdatesService } from "../auth/user-updates.service";
import { DeviceUsersService } from "../auth/device-users.service";
import { ServerMessagesService } from "./server-messages.service";

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.scss"]
})
export class GameComponent implements OnInit, OnDestroy {
  openPanel: string = "firstPresidency";
  subscription: Subscription;
  serverMessages: Array<string>;

  constructor(
    public readonly authService: DeviceUsersService,
    public readonly answersService: AnswersService,
    public readonly answerBusService: AnswerBusService,
    private readonly userUpdates: UserUpdatesService,
    private readonly serverMessagesService: ServerMessagesService
  ) {}

  ngOnInit() {
    this.subscription = this.answerBusService.listenForQuestionsAndAnswers();
    this.subscription.add(this.userUpdates.startListener());
    this.subscription.add(
      this.serverMessagesService.subscribe(
        serverMessages => (this.serverMessages = serverMessages)
      )
    );
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

  showShareButton() {
    return !!(navigator as any).share;
  }

  share() {
    (navigator as any).share({
      title: "Let's Play FantasyGC.org",
      url: "https://fantasygc.org"
    });
  }
}
