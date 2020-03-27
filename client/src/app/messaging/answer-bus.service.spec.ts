import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ChatBusService } from "../chat/chat-bus.service";
import { AnswerBusService } from "./answer-bus.service";
import { MessageBusService } from "./message-bus.service";

describe("AnswerBusService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        AnswerBusService,
        { provide: ChatBusService, useValue: {} },
        {
          provide: MessageBusService,
          useValue: { messageSender: () => void 0 }
        }
      ],
      imports: [HttpClientTestingModule]
    })
  );

  it("should be created", () => {
    const service: AnswerBusService = TestBed.inject(AnswerBusService);
    expect(service).toBeTruthy();
  });
});
