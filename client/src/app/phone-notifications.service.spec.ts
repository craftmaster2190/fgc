import { TestBed } from "@angular/core/testing";

import { PhoneNotificationsService } from "./phone-notifications.service";

describe("PhoneNotificationsService", () => {
  let service: PhoneNotificationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PhoneNotificationsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
