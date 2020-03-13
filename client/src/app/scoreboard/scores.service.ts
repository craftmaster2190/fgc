import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { Score } from "./score";
import { mapMessageTo } from "../util/map-message-to";
import { UserUpdatesService } from "../auth/user-updates.service";
import { merge } from "rxjs";

@Injectable()
export class ScoresService {
  private readonly scoresCache = {};

  constructor(
    private readonly userUpdates: UserUpdatesService,
    private readonly httpClient: HttpClient,
    private readonly messageBus: MessageBusService
  ) {}

  listenToScores(subscription: (scores) => void) {
    const listener = (scores: Array<Score>) => {
      scores.forEach(score => {
        this.scoresCache[score.userOrFamilyId] = score;
        this.userUpdates.requestUserIfNeeded(score.userOrFamilyId);
      });
      subscription(Object.values(this.scoresCache));
    };
    return merge(
      this.messageBus.topicWatcher("score"),
      this.messageBus.userTopicWatcher("score")
    )
      .pipe(mapMessageTo<Score>())
      .subscribe(listener);
  }

  getUserCount() {
    return this.httpClient.get<number>("/api/user/count").toPromise();
  }

  getUsernames() {
    return this.httpClient.get<Array<string>>("/api/user/all").toPromise();
  }
}
