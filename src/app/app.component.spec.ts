import { AppComponent } from "./app.component";
import { async, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { ToastService } from "./view/util/toast/toast.service";
import { Subscription, of } from "rxjs";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";

describe("AppComponent", () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        {
          provide: ToastService,
          useValue: { subscribe: () => of().subscribe() }
        }
      ],
      declarations: [AppComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  it("should create the app", () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });
});
