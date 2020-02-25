import { TestBed } from "@angular/core/testing";

import { ScoresService } from "./scores.service";

describe("ScoresService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ScoresService,
          useValue: {}
        }
      ]
    })
  );

  it("should be created", () => {
    const service: ScoresService = TestBed.get(ScoresService);
    expect(service).toBeTruthy();
  });
});
