import { TestBed } from "@angular/core/testing";

import { RecoverService } from "./recover.service";
import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("RecoverService", () => {
  let service: RecoverService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(RecoverService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
