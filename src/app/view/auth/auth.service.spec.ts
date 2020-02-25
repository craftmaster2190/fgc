import { AuthService } from "./auth.service";
import { TestBed } from "@angular/core/testing";

describe("AuthService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: {} }]
    })
  );

  it("should be created", () => {
    const service: AuthService = TestBed.get(AuthService);
    expect(service).toBeTruthy();
  });
});
