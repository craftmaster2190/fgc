import { TestBed } from "@angular/core/testing";

import { ImagesCacheService } from "./images-cache.service";
import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("ImagesCacheService", () => {
  let service: ImagesCacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(ImagesCacheService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
