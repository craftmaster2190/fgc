import { Chat } from './chat';
import { ChatBusService } from './chat-bus.service';
import { Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
  styleUrls: ["./chat.component.sass"]
})
export class ChatComponent implements OnInit, OnDestroy {
  chats: Array<Chat> = [];
  chatValue: string;
  loading: boolean;

  @ViewChild("chatValues", { static: false }) chatValues: ElementRef;

  private subscription: Subscription;

  constructor(private readonly chatBusService: ChatBusService) {}

  ngOnInit() {
    this.chatBusService
      .getAll()
      .then(chats => {
        Object.keys(chats)
          .sort()
          .forEach(time => {
            this.chats.push(chats[time]);
          });
        this.scrollToBottomOfChats();
      })
      .catch(() => {
        console.log("Unable to load chats.");
      })
      .then(() => {
        this.loading = false;
      });

    this.subscription = this.chatBusService.listen(chat => {
      this.chats.push(chat);
      this.scrollToBottomOfChats();
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  sendChat() {
    this.chatBusService.send(this.chatValue);
    this.chatValue = "";
  }

  scrollToBottomOfChats() {
    setTimeout(() => {
      const chatValuesDiv = this.chatValues.nativeElement as HTMLElement;
      chatValuesDiv.focus();
      chatValuesDiv.scrollTo(0, chatValuesDiv.scrollHeight);
    }, 500);
  }
}
