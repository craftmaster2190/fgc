import { TestBed } from "@angular/core/testing";

import { ScoresService } from "./scores.service";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { MessageBusService } from "src/app/messaging/message-bus.service";

describe("ScoresService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [ScoresService, { provide: MessageBusService, useValue: {} }],
      imports: [HttpClientTestingModule]
    })
  );

  it("should be created", () => {
    const service: ScoresService = TestBed.inject(ScoresService);
    expect(service).toBeTruthy();
  });
});
