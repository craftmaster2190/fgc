import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { of } from "rxjs";
import { DeviceUsersService } from "../auth/device-users.service";
import { RecoverService } from "./recover.service";
import { WelcomeComponent } from "./welcome.component";

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
        },
        {
          provide: RecoverService,
          useValue: {}
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
