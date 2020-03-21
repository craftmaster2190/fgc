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
import { Chat } from "./chat";
import { ChatBusService } from "./chat-bus.service";
import { ToastService } from "../toast/toast.service";
import { Time } from "./time";
import { UserUpdatesService } from "../auth/user-updates.service";

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
  styleUrls: ["./chat.component.scss"]
})
export class ChatComponent implements OnInit, OnDestroy {
  chatIds: Array<string> = [];

  chats: { [time: string]: Chat } = {};
  chatValue: string;

  @ViewChild("chatValues", { static: false }) chatValues: ElementRef;

  private subscription: Subscription;

  constructor(
    private readonly userUpdates: UserUpdatesService,
    private readonly chatBusService: ChatBusService,
    private readonly toastService: ToastService,
    private readonly authService: DeviceUsersService
  ) {}

  ngOnInit() {
    this.subscription = this.chatBusService.listen(chat => {
      this.chats[`${chat.id.epochSecond}.${chat.id.nano}`] = chat;
      this.chatIds = Object.keys(this.chats).sort();
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
    return Optional.of(chat?.id?.epochSecond)
      .map(moment.unix)
      .filter(time => time.isBefore(moment().subtract(30, "seconds")))
      .map(time => " - " + time.fromNow())
      .orElse(null);
  }

  notifyIfNeeded(chat: Chat) {
    if (
      this.authService.getCurrentUser()?.id !== chat.userId &&
      Optional.of(chat?.id?.epochSecond)
        .map(moment.unix)
        .map(time => time.isAfter(moment().subtract(30, "seconds")))
        .orElse(false)
    ) {
      this.toastService.create({
        message:
          this.userUpdates
            .getIfPresent(chat.userId)
            .map(user => user.name + ": ")
            .orElse("") + chat.value,
        classname: "bg-light"
      });
    }
  }

  getUserFromChatId(chatId) {
    return this.userUpdates
      .getIfPresent(this.chats[chatId].userId)
      .orElse(null);
  }
}
