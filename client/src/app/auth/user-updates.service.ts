import { Injectable } from "@angular/core";
import { merge } from "rxjs";
import { Family } from "../family/family";
import { ImagesCacheService } from "../image/images-cache.service";
import { MessageBusService } from "../messaging/message-bus.service";
import { mapMessageTo } from "../util/map-message-to";
import { Optional } from "../util/optional";
import { User } from "./user";

@Injectable({
  providedIn: "root"
})
export class UserUpdatesService {
  private readonly users: { [userOrFamilyId: string]: User | Family } = {};

  constructor(
    private readonly messageBusService: MessageBusService,
    private readonly imagesCache: ImagesCacheService
  ) {}

  startListener() {
    return merge(
      this.messageBusService.topicWatcher("updated-user"),
      this.messageBusService.userTopicWatcher("updated-user")
    )
      .pipe(mapMessageTo<User>())
      .subscribe(users => {
        users
          .map(user => {
            this.users[user.id] = user;
            Optional.of(user.family).map(family => {
              family.isFamily = true;
              this.users[family.id] = family;
            });

            return user.id;
          })
          .forEach(userId =>
            this.imagesCache.invalidate("/api/user/profile/" + userId)
          );
      });
  }

  requestUser(userId) {
    this.messageBusService.messageSender(`get-user`).convertAndSend(userId);
  }

  requestUserIfNeeded(userId) {
    if (!this.users[userId]) {
      this.requestUser(userId);
    }
  }

  getIfPresent(userId): Optional<User> {
    return Optional.of(this.users[userId]);
  }
}
