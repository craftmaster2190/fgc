import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { DeviceUsersService } from "../auth/device-users.service";
import { NavHeaderComponent } from "./nav-header.component";

describe("NavHeaderComponent", () => {
  let component: NavHeaderComponent;
  let fixture: ComponentFixture<NavHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavHeaderComponent],
      providers: [
        {
          provide: DeviceUsersService,
          useValue: {
            getCurrentUser: () => ({})
          }
        }
      ],
      imports: [RouterTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
