import { ScoresService } from "./scores.service";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { DeviceUsersService } from "../auth/device-users.service";
import { Subscription } from "rxjs";
import { Score } from "./score";
import { UserUpdatesService } from "../auth/user-updates.service";
import { Family } from "../family/family";
import { User } from "../auth/user";
import { filter } from "rxjs/operators";

@Component({
  selector: "app-scoreboard",
  templateUrl: "./scoreboard.component.html",
  styleUrls: ["./scoreboard.component.scss"]
})
export class ScoreboardComponent implements OnInit, OnDestroy {
  userCount: number;
  scores: Array<Score>;
  familyMembers: Array<string>;
  currentFamily?: Family;

  subscription: Subscription;
  familyScoringOpen: boolean;

  constructor(
    public readonly scoresService: ScoresService,
    private readonly userUpdates: UserUpdatesService,
    private readonly authService: DeviceUsersService
  ) {}

  ngOnInit() {
    this.currentFamily = this.authService.getCurrentUser()?.family;

    this.scoresService
      .getUserCount()
      .then(userCount => (this.userCount = userCount));

    this.subscription = this.scoresService.listenToScores(scores => {
      this.scores = scores
        .filter(score => {
          const user = this.getUserOrFamily(score.userOrFamilyId);
          return score.score > 0 && (!user || !(user as User).isAdmin);
        })
        .sort((a, b) => {
          let aIsBeforeB = false;
          if (a.score > b.score) {
            aIsBeforeB = true;
          } else if (a.score === b.score) {
            const aUser = this.getUserOrFamily(a.userOrFamilyId);
            const bUser = this.getUserOrFamily(b.userOrFamilyId);
            if (aUser && bUser) {
              if (!(aUser as Family)?.isFamily && (bUser as Family)?.isFamily) {
                aIsBeforeB = true;
              } else if (
                (aUser as Family)?.isFamily &&
                !(bUser as Family)?.isFamily
              ) {
                aIsBeforeB = false;
              } else {
                aIsBeforeB = aUser.name.localeCompare(bUser.name) === -1;
              }
            }
          }
          return aIsBeforeB ? -1 : 1;
        });

      scores.forEach(score =>
        this.userUpdates.requestUserIfNeeded(score.userOrFamilyId)
      );
    });

    this.authService
      .getFamilyMembers()
      .subscribe(members => (this.familyMembers = members));
  }

  isMe(score: Score) {
    return this.authService.getCurrentUser().id === score.userOrFamilyId;
  }

  getUserOrFamily(userOrFamilyId: string): User | Family | undefined {
    return this.userUpdates.getIfPresent(userOrFamilyId).orElseGet(() => {
      this.userUpdates.requestUserIfNeeded(userOrFamilyId);
      return null;
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
