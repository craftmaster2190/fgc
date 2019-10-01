import { FamilySearchService } from './family-search.service';
import { TestBed } from '@angular/core/testing';


describe("FamilySearchService", () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it("should be created", () => {
    const service: FamilySearchService = TestBed.get(FamilySearchService);
    expect(service).toBeTruthy();
  });
});
