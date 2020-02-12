import { Chat } from "./chat";
import { HttpClient } from "@angular/common/http";
import { IMessage } from "@stomp/stompjs";
import { Injectable, OnDestroy } from "@angular/core";
import { JSONMessageSender } from "src/app/model/messaging/json-message-sender";
import { MessageBusService } from "src/app/model/messaging/message-bus.service";
import { Subscription } from "rxjs";
import { ToastService } from "../util/toast/toast.service";
import { merge } from "rxjs";

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
    return merge(
      this.messageBusService.topicWatcher("chat")
      // this.messageBusService.userTopicWatcher("chat")
    ).subscribe(message => {
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
