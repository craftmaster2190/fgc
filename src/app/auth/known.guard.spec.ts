import { TestBed } from "@angular/core/testing";

import { KnownGuard } from "./known.guard";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { DeviceUsersService } from "./device-users.service";

describe("KnownGuard", () => {
  let guard: KnownGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DeviceUsersService, useValue: {} }],
      imports: [RouterTestingModule]
    });
    guard = TestBed.inject(KnownGuard);
  });

  it("should be created", () => {
    expect(guard).toBeTruthy();
  });
});
