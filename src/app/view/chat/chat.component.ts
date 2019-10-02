import { Chat } from './chat';
import { ChatBusService } from './chat-bus.service';
import { Component, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.sass']
})
export class ChatComponent implements OnInit, OnDestroy {

  chats: Array<Chat>;

  constructor(private readonly chatBusService: ChatBusService) { }

  ngOnInit() {

  }

  ngOnDestroy() {
    
  }

}
