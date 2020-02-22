import { AuthService } from "./auth.service";
import { TestBed } from "@angular/core/testing";import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("AuthService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [AuthService],
      imports: [HttpClientTestingModule]
    })
  );

  it("should be created", () => {
    const service: AuthService = TestBed.get(AuthService);
    expect(service).toBeTruthy();
  });
});
