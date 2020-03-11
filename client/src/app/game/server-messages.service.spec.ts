import { TestBed } from "@angular/core/testing";

import { ServerMessagesService } from "./server-messages.service";
import { MessageBusService } from "../messaging/message-bus.service";

describe("ServerMessagesService", () => {
  let service: ServerMessagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: MessageBusService,
          useValue: { messageSender: () => void 0 }
        }
      ]
    });
    service = TestBed.inject(ServerMessagesService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
