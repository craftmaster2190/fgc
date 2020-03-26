import { TestBed } from "@angular/core/testing";

import { BrowserDetectService } from "./browser-detect.service";

describe("BrowserDetectService", () => {
  let service: BrowserDetectService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BrowserDetectService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
