import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { DeviceUsersService } from "../auth/device-users.service";
import { NavHeaderComponent } from "./nav-header.component";
import { NgbModalModule } from "@ng-bootstrap/ng-bootstrap";
import { ImageComponent } from "../image/image.component";
import { of } from "rxjs";

describe("NavHeaderComponent", () => {
  let component: NavHeaderComponent;
  let fixture: ComponentFixture<NavHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavHeaderComponent, ImageComponent],
      providers: [
        {
          provide: DeviceUsersService,
          useValue: {
            getCurrentUser: () => ({}),
            getFamilyChangeEnabled: () => of(false)
          }
        }
      ],
      imports: [RouterTestingModule, NgbModalModule]
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
