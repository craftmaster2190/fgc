import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { Score } from "./score";

@Injectable()
export class ScoresService {
  private readonly scoresCache = {};

  constructor(
    private readonly httpClient: HttpClient,
    private readonly messageBus: MessageBusService
  ) {}

  listenToScores() {
    const listener = message => {
      const parsedScores = JSON.parse(message.body) as Score | Score[];
      const scores = Array.isArray(parsedScores)
        ? parsedScores
        : [parsedScores];
      scores.forEach(score => (this.scoresCache[score.userOrFamilyId] = score));
    };
    return this.messageBus
      .topicWatcher("score")
      .subscribe(listener)
      .add(this.messageBus.userTopicWatcher("score").subscribe(listener));
  }

  getUserCount() {
    return this.httpClient.get<number>("/api/user/count").toPromise();
  }
  getUsernames() {
    return this.httpClient.get<Array<string>>("/api/user/all").toPromise();
  }
}
