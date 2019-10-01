import { RxStompService } from '@stomp/ng2-stompjs';
export class AnswerSender {
  constructor(
    private readonly topic: string,
    private readonly rxStompService: RxStompService
  ) {}
  send = (message: string) =>
    this.rxStompService.publish({ destination: this.topic, body: message });
  convertAndSend = (message: any) => this.send(JSON.stringify(message));
}
