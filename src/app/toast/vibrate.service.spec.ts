import { TestBed } from "@angular/core/testing";

import { VibrateService } from "./vibrate.service";

describe("VibrateService", () => {
  let service: VibrateService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [VibrateService] });
    service = TestBed.inject(VibrateService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
