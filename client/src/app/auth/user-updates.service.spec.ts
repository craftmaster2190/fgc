import { TestBed } from "@angular/core/testing";

import { UserUpdatesService } from "./user-updates.service";
import { ImagesCacheService } from "../image/images-cache.service";
import { MessageBusService } from "../messaging/message-bus.service";

describe("UserUpdatesService", () => {
  let service: UserUpdatesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: MessageBusService, useValue: {} },
        { provide: ImagesCacheService, useValue: {} }
      ]
    });
    service = TestBed.inject(UserUpdatesService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
