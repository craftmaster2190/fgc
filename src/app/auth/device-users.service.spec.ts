import { TestBed } from "@angular/core/testing";

import { DeviceUsersService } from "./device-users.service";
import { DeviceIdService } from "./device-id.service";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";

describe("DeviceUsersService", () => {
  let service: DeviceUsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DeviceIdService, useValue: {} }],
      imports: [HttpClientTestingModule, RouterTestingModule]
    });
    service = TestBed.inject(DeviceUsersService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
