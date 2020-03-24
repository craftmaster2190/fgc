import { ScoresService } from "./scores.service";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { DeviceUsersService } from "../auth/device-users.service";
import { Subscription } from "rxjs";
import { Score } from "./score";
import { UserUpdatesService } from "../auth/user-updates.service";
import { Family } from "../family/family";
import { User } from "../auth/user";
import { filter } from "rxjs/operators";
import { MessageBusService } from "../messaging/message-bus.service";
import { Optional } from "../util/optional";

@Component({
  selector: "app-scoreboard",
  templateUrl: "./scoreboard.component.html",
  styleUrls: ["./scoreboard.component.scss"]
})
export class ScoreboardComponent implements OnInit, OnDestroy {
  userCount: number;
  scores: Array<Score>;
  familyScores: Array<Score>;
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

    this.familyScores = [
      {
        userOrFamilyId: this.authService.getCurrentUser()?.id,
        score: "..." as any
      }
    ];
    Optional.of(this.currentFamily?.id)
      .map<Score>(id => ({ userOrFamilyId: id, score: "..." as any }))
      .map(score => this.familyScores.push(score));

    this.scoresService
      .getUserCount()
      .then(userCount => (this.userCount = userCount));

    this.subscription = this.scoresService.listenToScores(scores => {
      this.scores = scores
        .filter(score => {
          const user = this.getUserOrFamily(score.userOrFamilyId);
          return score.score > 0 && (!user || !(user as User).isAdmin);
        })
        .sort(this.sortScores);

      this.subscription.add(
        this.scoresService
          .observeFamilyScores()
          .subscribe(
            scores => (this.familyScores = scores.sort(this.sortScores))
          )
      );

      scores.forEach(score =>
        this.userUpdates.requestUserIfNeeded(score.userOrFamilyId)
      );
    });
  }

  private sortScores = (aScore: Score, bScore: Score) => {
    let aIsBeforeB = false;
    if (aScore.score > bScore.score) {
      aIsBeforeB = true;
    } else if (aScore.score === bScore.score) {
      const aUser = this.getUserOrFamily(aScore.userOrFamilyId);
      const bUser = this.getUserOrFamily(bScore.userOrFamilyId);
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
  };

  isMyScore(score: Score) {
    return (
      this.authService.getCurrentUser()?.id === score.userOrFamilyId ||
      this.currentFamily?.id === score.userOrFamilyId
    );
  }

  getUserOrFamily(userOrFamilyId: string): User | Family | undefined {
    return this.userUpdates.getIfPresent(userOrFamilyId).orElseGet(() => {
      this.userUpdates.requestUserIfNeeded(userOrFamilyId);
      return null;
    });
  }

  getScorerType(score: Score): string {
    return (this.getUserOrFamily(score.userOrFamilyId) as Family)?.isFamily
      ? "Family"
      : this.isMyScore(score)
      ? "Me"
      : "Player";
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
