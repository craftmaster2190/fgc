import { Injectable } from "@angular/core";
import { MessageBusService } from "../messaging/message-bus.service";
import { ImagesCacheService } from "../image/images-cache.service";

@Injectable({
  providedIn: "root"
})
export class UserUpdatesService {
  constructor(
    private readonly messageBusService: MessageBusService,
    private readonly imagesCache: ImagesCacheService
  ) {}

  startListener() {
    return this.messageBusService
      .topicWatcher("updated-userid")
      .subscribe(message => {
        try {
          const parsedUserIds = JSON.parse(message.body) as string | string[];
          const userIds = Array.isArray(parsedUserIds)
            ? parsedUserIds
            : [parsedUserIds];
          userIds.forEach(userId =>
            this.imagesCache.invalidate("/api/user/profile/" + userId)
          );
        } catch (error) {
          console.error(
            "Unable to read invalidate user id",
            message.body,
            error
          );
        }
      });
  }
}
