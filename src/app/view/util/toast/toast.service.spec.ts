import { ToastService } from './toast.service';
import { TestBed } from '@angular/core/testing';


describe("ToastService", () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it("should be created", () => {
    const service: ToastService = TestBed.get(ToastService);
    expect(service).toBeTruthy();
  });
});
