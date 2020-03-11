import { Injectable } from "@angular/core";
import { merge } from "rxjs";
import { JSONMessageSender } from "src/app/messaging/json-message-sender";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { Chat } from "./chat";
import { ToastService } from "../toast/toast.service";

@Injectable()
export class ChatBusService {
  private readonly chatSender: JSONMessageSender;

  constructor(
    private readonly messageBusService: MessageBusService,
    private readonly toastService: ToastService
  ) {
    this.chatSender = messageBusService.messageSender("chat");
  }

  send(message: string) {
    this.chatSender.send(message);
  }

  listen(next: (chat: Chat) => void) {
    return this.messageBusService.topicWatcher("chat").subscribe(message => {
      try {
        const parsedChats = JSON.parse(message.body) as Chat | Chat[];
        const chats = Array.isArray(parsedChats) ? parsedChats : [parsedChats];
        chats.forEach(chat => next(chat));
      } catch (error) {
        this.toastService.createError(
          "Unable to read chat",
          message.body + "\n" + error
        );
      }
    });
  }
}
