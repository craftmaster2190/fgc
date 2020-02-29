import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { MessageBusService } from "src/app/messaging/message-bus.service";
import { ChatBusService } from "./chat-bus.service";
import { ToastService } from "../util/toast/toast.service";

describe("ChatBusService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        ChatBusService,
        {
          provide: MessageBusService,
          useValue: { messageSender: () => void 0 }
        },
        {
          provide: ToastService,
          useValue: {}
        }
      ],
      imports: [HttpClientTestingModule]
    })
  );

  it("should be created", () => {
    const service: ChatBusService = TestBed.get(ChatBusService);
    expect(service).toBeTruthy();
  });
});
