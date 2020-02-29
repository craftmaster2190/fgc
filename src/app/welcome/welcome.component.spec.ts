import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { WelcomeComponent } from "./welcome.component";
import { DeviceUsersService } from "../auth/device-users.service";
import { RouterTestingModule } from "@angular/router/testing";
import { of } from "rxjs";
import { promise } from "protractor";

describe("WelcomeComponent", () => {
  let component: WelcomeComponent;
  let fixture: ComponentFixture<WelcomeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WelcomeComponent],
      providers: [
        {
          provide: DeviceUsersService,
          useValue: {
            getCurrentUser: () => of({}),
            getUsers: () => of([]),
            fetchUsers: () => Promise.resolve()
          }
        }
      ],
      imports: [RouterTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WelcomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
