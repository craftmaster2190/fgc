import { AnswersService } from "./answers.service";
import { TestBed } from "@angular/core/testing";

describe("AnswersService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({ providers: [AnswersService] })
  );

  it("should be created", () => {
    const service: AnswersService = TestBed.get(AnswersService);
    expect(service).toBeTruthy();
  });
});