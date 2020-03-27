import { FamilySearchService } from "./family-search.service";
import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("FamilySearchService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [FamilySearchService],
      imports: [HttpClientTestingModule]
    })
  );

  it("should be created", () => {
    const service: FamilySearchService = TestBed.inject(FamilySearchService);
    expect(service).toBeTruthy();
  });
});
