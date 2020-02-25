import { AnswerBusService } from "./answer-bus.service";
import { TestBed } from "@angular/core/testing";

describe("AnswerBusService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        {
          provide: AnswerBusService, // most likely need the actual object, not a provider
          useValue: {}
        }
      ]
    })
  );

  it("should be created", () => {
    const service: AnswerBusService = TestBed.get(AnswerBusService);
    expect(service).toBeTruthy();
  });
});
