import { JSONMessageSender } from "./json-message-sender";
import { IMessage } from "@stomp/stompjs";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { RxStompService } from "@stomp/ng2-stompjs";
@Injectable()
export class MessageBusService {
  constructor(private readonly rxStompService: RxStompService) {}
  private readonly senders: Record<string, JSONMessageSender> = {};
  private readonly watchers: Record<string, Observable<IMessage>> = {};

  messageSender = (topic: string) => {
    topic = `/app/${topic}`;
    this.senders[topic] =
      this.senders[topic] || new JSONMessageSender(topic, this.rxStompService);
    return this.senders[topic];
  }

  topicWatcher = (topic: string) => {
    topic = `/topic/${topic}`;
    this.watchers[topic] =
      this.watchers[topic] || this.rxStompService.watch(topic);
    return this.watchers[topic];
  }
}
