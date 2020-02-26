import { TestBed } from "@angular/core/testing";

import { DeviceUsersService } from "./device-users.service";

describe("DeviceUsersService", () => {
  let service: DeviceUsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeviceUsersService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
