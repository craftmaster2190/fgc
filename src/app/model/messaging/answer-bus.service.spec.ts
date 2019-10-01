import { AnswerBusService } from './answer-bus.service';
import { TestBed } from '@angular/core/testing';


describe("AnswerBusService", () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it("should be created", () => {
    const service: AnswerBusService = TestBed.get(AnswerBusService);
    expect(service).toBeTruthy();
  });
});
