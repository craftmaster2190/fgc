import { Injectable } from "@angular/core";
import { merge } from "rxjs";
import { JSONMessageSender } from "src/app/messaging/json-message-sender";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { Chat } from "./chat";
import { ToastService } from "../toast/toast.service";
import { mapMessageTo } from "../util/map-message-to";
import { UserUpdatesService } from "../auth/user-updates.service";

@Injectable()
export class ChatBusService {
  private readonly chatSender: JSONMessageSender;

  constructor(
    private readonly messageBusService: MessageBusService,
    private readonly userUpdates: UserUpdatesService,
    private readonly toastService: ToastService
  ) {
    this.chatSender = messageBusService.messageSender("chat");
  }

  send(message: string) {
    this.chatSender.send(message);
  }

  listen(next: (chat: Chat) => void) {
    return this.messageBusService
      .topicWatcher("chat")
      .pipe(mapMessageTo<Chat>())
      .subscribe(chats =>
        chats.forEach(chat => {
          this.userUpdates.requestUserIfNeeded(chat.userId);
          next(chat);
        })
      );
  }
}
