import { ToastService } from "./toast.service";
import { TestBed } from "@angular/core/testing";

describe("ToastService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [ToastService]
    })
  );

  it("should be created", () => {
    const service: ToastService = TestBed.inject(ToastService);
    expect(service).toBeTruthy();
  });
});
