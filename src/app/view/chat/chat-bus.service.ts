import { Chat } from './chat';
import { HttpClient } from '@angular/common/http';
import { IMessage } from '@stomp/stompjs';
import { Injectable, OnDestroy } from '@angular/core';
import { JSONMessageSender } from 'src/app/model/messaging/json-message-sender';
import { MessageBusService } from 'src/app/model/messaging/message-bus.service';
import { Subscription } from 'rxjs';

@Injectable()
export class ChatBusService {
  private readonly chatSender: JSONMessageSender;

  constructor(private readonly messageBusService: MessageBusService, private readonly httpClient: HttpClient) {
    this.chatSender = messageBusService.messageSender("chat");
  }

  getAll() {
    return this.httpClient.get("/api/chat").toPromise();
  }

  send(message: string) {
    this.chatSender.send(message);
  }

  listen(next: (message: IMessage) => void) {
    return this.messageBusService.topicWatcher("chat").subscribe(next);
  }
}
