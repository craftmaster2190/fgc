import { ScoresService } from "./scores.service";
import { AuthService } from "../auth/auth.service";
import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-scoreboard",
  templateUrl: "./scoreboard.component.html",
  styleUrls: ["./scoreboard.component.scss"]
})
export class ScoreboardComponent implements OnInit {
  private interval;
  userCount: number;
  usernames: Array<string>;
  userScores: Array<{ name: string; value: number }> = [];
  familyScores: Array<{ name: string; value: number }> = [];
  constructor(
    private readonly scoresService: ScoresService,
    private readonly authService: AuthService
  ) {}

  ngOnInit() {
    this.loadScores();
    this.interval = setInterval(() => {
      this.loadScores();
    }, 30000);
  }

  private loadScores() {
    if (this.authService.getLoggedInUser().isAdmin) {
      this.scoresService
        .getUsernames()
        .then(usernames => (this.usernames = usernames));
    }
    this.scoresService
      .getUserCount()
      .then(userCount => (this.userCount = userCount));

    this.scoresService.get().then(scores => {
      this.userScores = Object.keys(scores.user2Score || {})
        .map(user => ({
          name: user,
          value: scores.user2Score[user]
        }))
        .sort((a, b) => (a.value === b.value ? 0 : a.value < b.value ? 1 : -1));
      this.familyScores = Object.keys(scores.family2Score || {})
        .map(family => ({
          name: family,
          value: scores.family2Score[family]
        }))
        .sort((a, b) => (a.value === b.value ? 0 : a.value < b.value ? 1 : -1));
    });
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }
}
