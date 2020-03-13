import { Injectable } from "@angular/core";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { map } from "rxjs/operators";
import { IMessage } from "@stomp/stompjs";
import { mapMessageTo } from "../util/map-message-to";

@Injectable({
  providedIn: "root"
})
export class ServerMessagesService {
  constructor(private readonly messageBusService: MessageBusService) {}

  subscribe(subscription: (serverMessages: Array<string>) => void) {
    return this.messageBusService
      .topicWatcher("server-messages")
      .pipe(mapMessageTo<string>())
      .subscribe(subscription);
  }
}
