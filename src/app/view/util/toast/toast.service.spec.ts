import { ToastService } from "./toast.service";
import { TestBed } from "@angular/core/testing";
import { of } from "rxjs";

describe("ToastService", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ToastService,
          useValue: { subscribe: () => of().subscribe() }
        }
      ]
    })
  );

  it("should be created", () => {
    const service: ToastService = TestBed.get(ToastService);
    expect(service).toBeTruthy();
  });
});
