import { FamilySearchService } from "./family-search.service";
import { TestBed } from "@angular/core/testing";
import { AuthService } from "../auth/auth.service";

describe("FamilySearchService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: {} },
        // most likey need the actual FamilySearchService
        { provide: FamilySearchService, useValue: {} }
      ]
    })
  );

  it("should be created", () => {
    const service: FamilySearchService = TestBed.get(FamilySearchService);
    expect(service).toBeTruthy();
  });
});
