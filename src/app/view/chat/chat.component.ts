import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from "@angular/core";
import * as moment from "moment";
import { Subscription } from "rxjs";
import { DeviceUsersService } from "../auth/device-users.service";
import { Optional } from "../util/optional";
import { ToastService } from "../util/toast/toast.service";
import { Chat } from "./chat";
import { ChatBusService } from "./chat-bus.service";

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
  styleUrls: ["./chat.component.scss"]
})
export class ChatComponent implements OnInit, OnDestroy {
  chats: Array<Chat> = [];
  chatValue: string;

  @ViewChild("chatValues", { static: false }) chatValues: ElementRef;

  private subscription: Subscription;

  constructor(
    private readonly chatBusService: ChatBusService,
    private readonly toastService: ToastService,
    private readonly authService: DeviceUsersService
  ) {}

  ngOnInit() {
    this.subscription = this.chatBusService.listen(chat => {
      this.chats.push(chat);
      this.chats.sort((a, b) => {
        if (a.time.epochSecond === b.time.epochSecond) {
          return a.time.nano > b.time.nano ? 1 : -1;
        }
        return a.time.epochSecond > b.time.epochSecond ? 1 : -1;
      });
      this.scrollToBottomOfChats();
      this.notifyIfNeeded(chat);
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  sendChat() {
    if (this.chatValue) {
      this.chatBusService.send(this.chatValue);
    }
    this.chatValue = "";
  }

  scrollToBottomOfChats() {
    setTimeout(() => {
      const chatValuesDiv = this.chatValues.nativeElement as HTMLElement;
      chatValuesDiv.focus();
      chatValuesDiv.scrollTo(0, chatValuesDiv.scrollHeight);
    }, 500);
  }

  renderTimeAgo(chat: Chat) {
    return Optional.of(chat?.time?.epochSecond)
      .map(moment.unix)
      .filter(time => time.isBefore(moment().subtract(30, "seconds")))
      .map(time => " - " + time.fromNow())
      .orElse(null);
  }

  notifyIfNeeded(chat: Chat) {
    if (
      this.authService.getCurrentUser()?.id !== chat.user?.id &&
      Optional.of(chat?.time?.epochSecond)
        .map(moment.unix)
        .map(time => time.isAfter(moment().subtract(30, "seconds")))
        .orElse(false)
    ) {
      this.toastService.create({
        message: chat.user?.name + ": " + chat.value,
        classname: "bg-secondary"
      });
    }
  }
}
