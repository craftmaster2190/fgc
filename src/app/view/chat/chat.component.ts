import { Chat } from './chat';
import { ChatBusService } from './chat-bus.service';
import { Component, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
  styleUrls: ["./chat.component.sass"]
})
export class ChatComponent implements OnInit, OnDestroy {
  chats: Array<Chat>;
  loading: boolean;

  constructor(private readonly chatBusService: ChatBusService) {}

  ngOnInit() {
    this.chatBusService.getAll().then(chats => {
      // this.chats = chats;
    })
    .catch(() => {
      console.log("Unable to load chats.");
    })
    .then(() => {
      this.loading = false;
    });
  }

  ngOnDestroy() {}
}
