import { ScoresService } from "./scores.service";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { DeviceUsersService } from "../auth/device-users.service";
import { Subscription } from "rxjs";
import { Score } from "./score";

@Component({
  selector: "app-scoreboard",
  templateUrl: "./scoreboard.component.html",
  styleUrls: ["./scoreboard.component.scss"]
})
export class ScoreboardComponent implements OnInit, OnDestroy {
  userCount: number;
  usernames: Array<string>;
  userScores: Array<{ name: string; value: number }> = [];
  familyScores: Array<{ name: string; value: number }> = [];

  subscription: Subscription;

  constructor(
    private readonly scoresService: ScoresService,
    private readonly authService: DeviceUsersService
  ) {}

  ngOnInit() {
    this.scoresService
      .getUserCount()
      .then(userCount => (this.userCount = userCount));

    this.subscription = this.scoresService.listenToScores();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
