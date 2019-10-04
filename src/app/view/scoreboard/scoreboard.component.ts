import { ScoresService } from './scores.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: "app-scoreboard",
  templateUrl: "./scoreboard.component.html",
  styleUrls: ["./scoreboard.component.sass"]
})
export class ScoreboardComponent implements OnInit {
  private interval;
  userScores: Array<{ name: string; value: number }> = [];
  familyScores: Array<{ name: string; value: number }> = [];
  constructor(private readonly scoresService: ScoresService) {}

  ngOnInit() {
    this.loadScores();
    this.interval = setInterval(() => {
      this.loadScores();
    }, 30000);
  }

  private loadScores() {
    this.scoresService.get().then(scores => {
      this.userScores = Object.keys(scores.user2Score).map(user => ({
        name: user,
        value: scores.user2Score[user]
      }));
      this.familyScores = Object.keys(scores.family2Score).map(family => ({
        name: family,
        value: scores.family2Score[family]
      }));
    });
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }
}
