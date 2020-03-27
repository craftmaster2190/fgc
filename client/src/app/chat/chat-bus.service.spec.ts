import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { ChatBusService } from "./chat-bus.service";
import { ToastService } from "../toast/toast.service";

describe("ChatBusService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        ChatBusService,
        {
          provide: MessageBusService,
          useValue: { messageSender: () => void 0 }
        }
      ],
      imports: [HttpClientTestingModule]
    })
  );

  it("should be created", () => {
    const service: ChatBusService = TestBed.inject(ChatBusService);
    expect(service).toBeTruthy();
  });
});
