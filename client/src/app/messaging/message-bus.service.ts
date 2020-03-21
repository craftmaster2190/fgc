import { Injectable } from "@angular/core";
import { RxStompService } from "@stomp/ng2-stompjs";
import { RxStompState } from "@stomp/rx-stomp";
import { IMessage } from "@stomp/stompjs";
import { Observable, Subject, interval } from "rxjs";
import { JSONMessageSender } from "./json-message-sender";
import { ToastService } from "../toast/toast.service";
import { debounce, throttle } from "rxjs/operators";
import { SentryErrorHandler } from "../config/sentry-error-handler.service";

@Injectable()
export class MessageBusService {
  private state: RxStompState;

  constructor(
    private readonly rxStompService: RxStompService,
    toastService: ToastService
  ) {
    const reconnectingWebsocket = new Subject<void>();
    reconnectingWebsocket
      .pipe(
        debounce(() => interval(500)),
        throttle(() => interval(8000))
      )
      .subscribe(() => {
        if (this.state !== RxStompState.OPEN) {
          toastService.create({
            message: "Reconnecting to server...",
            classname: "bg-warning"
          });
        }
      });

    rxStompService.connectionState$.subscribe(state => {
      console.log("WebSocket:", RxStompState[state]);
      if (state !== RxStompState.CONNECTING && state !== RxStompState.OPEN) {
        reconnectingWebsocket.next();
      }
      this.state = state;
    });
  }
  private readonly senders: Record<string, JSONMessageSender> = {};
  private readonly watchers: Record<string, Observable<IMessage>> = {};

  messageSender = (topic: string) => {
    topic = `/app/${topic}`;
    this.senders[topic] =
      this.senders[topic] || new JSONMessageSender(topic, this.rxStompService);
    return this.senders[topic];
  };

  topicWatcher = (topic: string) => {
    topic = `/topic/${topic}`;
    this.watchers[topic] =
      this.watchers[topic] || this.rxStompService.watch(topic);
    return this.watchers[topic];
  };

  userTopicWatcher = (topic: string) => {
    topic = `/user/topic/${topic}`;
    this.watchers[topic] =
      this.watchers[topic] || this.rxStompService.watch(topic);
    return this.watchers[topic];
  };
}
